package com.test.vaadintest;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;




import com.vaadin.tapio.googlemaps.client.LatLon;

public class Database {

	private Connection conn = null;
	
	/**
	 * Konstruktor az adatbázis betöltéséhez/létrehozásához.
	*/
	public Database(){
		try {
		  if (conn == null) connectToDb();
          Statement stmt = conn.createStatement();
          
          String users = "CREATE TABLE IF NOT EXISTS users("
          		+ "username CHAR(20) PRIMARY KEY NOT NULL,"
          		+ "password CHAR(20) NOT NULL)";
          
          String parking = "CREATE TABLE IF NOT EXISTS parking("
          		+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
          		+ "username CHAR(20) NOT NULL,"
          		+ "lat REAL,"
          		+ "lon REAL,"
          		+ "address TEXT,"
          		+ "price REAL,"
          		+ "availfrom TEXT,"
          		+ "availuntil TEXT,"
          		+ "FOREIGN KEY(username) REFERENCES users(username)"
          		+ ")";
          
          String parkrating = "CREATE TABLE IF NOT EXISTS parkrating("
          		+ "id INTEGER NOT NULL,"
          		+ "username CHAR(20) NOT NULL,"
          		+ "picture TEXT,"
          		+ "rating INT CHECK( rating >=1 AND rating <= 5 ),"
          		+ "comment TEXT,"
          		+ "PRIMARY KEY (id, username),"
          		+ "FOREIGN KEY(id) REFERENCES parking(id),"
          		+ "FOREIGN KEY(username) REFERENCES users(username)"
          		+ ")";
          
          stmt.executeUpdate(users);
          stmt.executeUpdate(parking);
          stmt.executeUpdate(parkrating);
          stmt.close();
          conn.commit();
          conn.close();
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.exit(0);
        }
	}
	
	private void connectToDb() throws SQLException{

        try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        conn = DriverManager.getConnection("jdbc:sqlite:park.db");
        conn.setAutoCommit(false);
        System.out.println("Opened database successfully");
	}
	
	private String encryptPass(String password){
		try {
			MessageDigest cript = MessageDigest.getInstance("SHA-1");
			cript.reset();
			cript.update(password.getBytes("utf8"));
			return  new String(cript.digest(), "utf8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Megnézi hogy a user helyes jelszót adott-e meg.
	 * @param name
	 * @param password
	 * @return
	 */
	public boolean checkUserAndPassword(String name, String password){
		String hashed = encryptPass(password);
		try {
			if (conn.isClosed()) connectToDb();
			String checkuser = "SELECT username, password FROM users "
					+ "WHERE username = ? AND password = ?";

			PreparedStatement stmt = conn.prepareStatement(checkuser);
	        stmt.setString(1, name);
	        stmt.setString(2, hashed);
	        ResultSet rec = stmt.executeQuery();
	        conn.close();
	        if (rec.next()){
	        	return true; //ResultSet.isLast() nincs implementálva... szóval nem ellenőrzöm le, 
	        	//hogy nem adott-e vissza véletlen többet (az adatmodell mondjuk tiltja)
	        }else return false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		return false;
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return Megmondja, hogy sikeres volt-e a beszúrás.
	 */
	public boolean addUser(String username, String password) {
		String adduser = "INSERT INTO users (username, password)"
				+ "VALUES (?, ?)";
		try {
			password = encryptPass(password);
			if (conn.isClosed()) connectToDb();
			PreparedStatement stmt = conn.prepareStatement(adduser);
			stmt.setString(1, username);
			stmt.setString(2, password);
			stmt.execute();
			stmt.close();
			conn.commit();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("User added");
		
		return true;
	}
	
	/**
	 * Ellenőrzi, hogy van-e már regisztráció ezzel a névvel.
	 * @param name
	 * @return
	 */
	public boolean isUsernameAlreadyTaken(String name){
		String checkusername = "SELECT * FROM users"
				+ " WHERE username = ? ";
		try {
			if (conn.isClosed()) connectToDb();
			PreparedStatement stmt = conn.prepareStatement(checkusername);
			stmt.setString(1, name);
			ResultSet result = stmt.executeQuery();
			conn.close();
			if (result.next()) return true;
			else return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Ellenőrzi, hogy létezik-e már parkolóhely ezzel az id-vel.
	 * @param name
	 * @return
	 */
	public boolean doParkingPLaceExist(int id){
		String checkis = "SELECT * FROM parking"
				+ " WHERE id = ? ";
		try {
			if (conn.isClosed()) connectToDb();
			PreparedStatement stmt = conn.prepareStatement(checkis);
			stmt.setInt(1, id);
			ResultSet result = stmt.executeQuery();
			conn.close();
			if (result.next()) return true;
			else return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Ellenőrzi, hogy Ez a user adott-e már hozzá ehhez a parkolóhoz ratinget
	 * ha igen visszatér az adatokkal, ha nem akkor nullával.
	 * A sorrend kötött: picture, rating, comment, ha valamelyik hiányzik, akkor az null
	 * @param name, id
	 * @return
	 */
	public ArrayList<Object> hasUserRatedThis(int id, String user){
		String checkis = "SELECT * FROM parkingrating"
				+ " WHERE id = ? AND username = ? ";
		if (id == 0 || user == null) return null;
		try {
			if (conn.isClosed()) connectToDb();
			PreparedStatement stmt = conn.prepareStatement(checkis);
			stmt.setInt(1, id);
			stmt.setString(2, user);
			ResultSet result = stmt.executeQuery();
			
			if (result.next()){
				ArrayList<Object> picComRate = new ArrayList<Object>();
				picComRate.add(result.getString("picture"));
				picComRate.add(new Integer(result.getInt("rating")));
				picComRate.add(result.getString("comment"));
				conn.close();
				return picComRate;
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean addParkingPlace(ParkingPlace pp){
		String addpark = "INSERT INTO parking (username, lat, lon, address, price, availfrom, availuntil)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		try {
			if (conn.isClosed()) connectToDb();
			PreparedStatement stmt = conn.prepareStatement(addpark);
			stmt.setString(1, pp.user);
			if (pp.lat != 0 && pp.lon != 0){
				stmt.setFloat(2, pp.lat);
				stmt.setFloat(3, pp.lon);
			}
			if (pp.address != null)
				stmt.setString(4, pp.address);
			if (pp.price != 0)
				stmt.setFloat(5, pp.price);
			if (pp.availfrom != null)
				stmt.setString(6, pp.availfrom);
			if (pp.availuntil!= null)
				stmt.setString(7, pp.availuntil);
			stmt.execute();
			stmt.close();
			conn.commit();
			
			if (!pp.imgs.isEmpty() || !pp.ratings.isEmpty() || !pp.comments.isEmpty()){
				String getmaxid = "SELECT MAX(id) AS maxid FROM parking";
				Statement query = conn.createStatement();
				ResultSet maxid = query.executeQuery(getmaxid);
				pp.setId(maxid.getInt("maxid"));
				conn.close(); //azert hogy lehessen addParkRatinget külön is hívni.
				addParkRating(pp);
			}
			
			if (!conn.isClosed()) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}
		System.out.println("Parking place added");
		return true;
	}
	
	/**
	 * Warning!! Nem túl logikus, de itt a pp-ből szedjük ki a módosító user nevét.
	 * @param pp
	 * @throws Exception
	 */
	public void addParkRating(ParkingPlace pp) throws Exception{
		String addrating = "INSERT INTO parkrating (id, username, picture, rating, comment)"
				+ "VALUES (?, ?, ?, ?, ?)";
		if (conn.isClosed()) connectToDb();
		PreparedStatement stmt = conn.prepareStatement(addrating);
		
		stmt.setInt(1, pp.getId());
		stmt.setString(2, pp.user);
		if (pp.imgs.get(0) != null){
			stmt.setString(3, pp.imgs.get(0));
		}
		if (pp.ratings.get(0) != null){
			stmt.setInt(4, pp.ratings.get(0));
		}
		if (pp.comments.get(0) != null){
			stmt.setString(5, pp.comments.get(0));
		}
		stmt.execute();
		conn.commit();
		conn.close();
	}
	
	
	/**
	 * Összetett lekérédst hajt végre az adatbázison.
	 * @param around Milyen földrajzi koordináta körül keressünk.
	 * @param distanceInGeoSecs Az adott földrajzi szélesség körül fördrajzi másodpercben mérve mekkora távolságra szűrjünk.
	 * @param maxprice 0 ha nem érdekes
	 * @param from
	 * @param until
	 * @return
	 */
	public ArrayList<ParkingPlace> queryParkingPlace(LatLon around, float distanceInGeoSecs, float maxprice, String from, String until){
		String baseQuery = "SELECT * FROM parking WHERE id IS NOT NULL "; //id IS NOT NULL csak azért, hogy bármilyen kombinációban lehessen AND
		String timeConstraint1 = " AND (time(availfrom) <= time( ? ) OR availfrom IS NULL) ";
		String timeConstraint2 = " AND (time(availuntil) >= time ( ? ) OR availuntil IS NULL) ";
		String priceConstraint = " AND price <= ? ";
		String distanceConstraint = " AND ABS ( lat - ? ) <= ? AND ABS( lon - ? ) <= ? ";
		if (around != null){
			baseQuery += distanceConstraint;
		}
		if (maxprice > 0) {
			baseQuery += priceConstraint;
		}
		if (from != null){
			baseQuery += timeConstraint1;
		}
		if (until != null) {
			baseQuery += timeConstraint2;
		}
		//ha lenne function pointer, akkor ez szebben nézne ki. 
		try {
			if (conn.isClosed()) connectToDb();
			PreparedStatement stmt = conn.prepareStatement(baseQuery);
			int stmtParamCount = 1;
			if (around != null){
				stmt.setFloat(1, (float) (around.getLat()));
				stmt.setFloat(2, (float) 90);
				stmt.setFloat(3, (float) (around.getLon()));
				stmt.setFloat(4, (float) 180);
				stmtParamCount += 4;
			}
			if (maxprice > 0) {
				stmt.setFloat(stmtParamCount, maxprice);
				stmtParamCount++;
			}
			if (from != null){
				stmt.setString(stmtParamCount, from);
				stmtParamCount++;
			}
			if (until != null) {
				stmt.setString(stmtParamCount, until);
				stmtParamCount++;
			}
			
			ResultSet res = stmt.executeQuery();
			ArrayList<ParkingPlace> ret = new ArrayList<ParkingPlace>();
			while (res.next()){
				ParkingPlace pp = new ParkingPlace(res.getString("username"), res.getFloat("lat"), 
						res.getFloat("lon"), res.getString("address"), res.getFloat("price"), res.getString("availfrom"), res.getString("availuntil"));
				pp.setId(res.getInt("id"));
				ret.add(pp);
			}
			conn.close();
			return ret;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Visszaadja egy parkolóhelyről az összes adatát a véleményekkel, értékeléssekkel képekkel együtt,
	 * ha kérjük a ratings paraméterrel.
	 * @param id
	 * @param ratings 
	 * @return
	 */
	public ParkingPlace queryAllDataOfOneParkingPlace(int id, boolean ratings){
		String query = "SELECT * FROM parking  ";
		if (ratings) query += " LEFT OUTER JOIN "
				+ " (SELECT id AS ratedid, username AS rater, picture, rating, comment FROM parkrating) "
				+ " ON parking.id = ratedid ";
		query += " WHERE id = ? ";
		try {
			if (conn.isClosed()) connectToDb();
			PreparedStatement stmt = conn.prepareStatement(query);
			
			stmt.setInt(1, id);
			ResultSet res = stmt.executeQuery();
			res.next();
			ParkingPlace pp = new ParkingPlace(res.getString("username"), res.getFloat("lat"), res.getFloat("lon"), 
					res.getString("address"), res.getFloat("price"), res.getString("availfrom"), res.getString("availuntil"));
			pp.setId(res.getInt("id"));
			if (ratings) {
				do {  
					pp.addImgRatingComment(res.getString("picture"), res.getInt("rating"), res
							.getString("comment"), res.getString("rater"));
				} while (res.next());
			}
			conn.close();
			return pp;
		} catch (SQLException e) {
			e.printStackTrace();
		}/* catch (IOException e) {
			e.printStackTrace();
		}*/
		return null;
	}
	
	/**
	 * Módosít teszőleges számú és kombinációjú mezőt a parking táblában.
	 * DE comment, értékelés, kép módosításához nem használható!
	 * @param pp
	 * @return
	 */
	public boolean modifyDataOfParkingPlace(ParkingPlace pp){
		String updateBase = "UPDATE parking SET ";
		if ( ! pp.hasAnyFieldGiven()) return false;
		else{
			HashMap<String,String> strings = new HashMap<String, String>();
			HashMap<String, Float> floats = new HashMap<String, Float>(); 
			if (pp.user != null)
				strings.put("username", pp.user);
			if (pp.lat != 0)
				floats.put("lat", pp.lat);
			if (pp.lon != 0)
				floats.put("lon", pp.lon);
			if (pp.price != 0)
				floats.put("price", pp.price);
			if (pp.address != null)
				strings.put("address", pp.address);
			if (pp.availfrom != null)
				strings.put("availfrom", pp.availfrom);
			if (pp.availuntil != null)
				strings.put("availuntil", pp.availuntil);
			
			Iterator<Entry<String, String>> its = strings.entrySet().iterator();
			while (its.hasNext())
				updateBase += its.next().getKey().concat(" = ? , ");
			Iterator<Entry<String, Float>> itf = floats.entrySet().iterator();
			while (itf.hasNext())
				updateBase += itf.next().getKey().concat(" = ? , ");
			
			updateBase = updateBase.substring(0, updateBase.length() - 2);
			updateBase += " WHERE id = ? ";
			try {
				if (conn.isClosed()) connectToDb();
				PreparedStatement stmt = conn.prepareStatement(updateBase);
				
				Iterator<Entry<String, String>> its2 = strings.entrySet().iterator();
				Iterator<Entry<String, Float>> itf2 = floats.entrySet().iterator();
				int idx = 1;
				
				while (its2.hasNext()){
					stmt.setString(idx, its2.next().getValue());
					idx++;
				}
				while (itf2.hasNext()){
					stmt.setFloat(idx, itf2.next().getValue());
					idx++;
				}
				
				stmt.setInt(idx, pp.getId());
				if (stmt.executeUpdate() == 0) { conn.close(); return false; }
				conn.commit();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * Az adatokat a parkolóhely azonosítójával és az értékelő user nevével azonosítjuk. ha valamelyik érték null, akkor azt nem bántja
	 * @param id
	 * @param user
	 * @return TRUE ha sikerült a módosítás
	 */
	public boolean modifyImgRatingCommentOfParkingPlace(int id, String username, String imgPath, Integer rating, String comment){
		String update = "UPDATE parkrating SET ";
		if (id == 0 || username == null || 
				(imgPath == null && rating == null && comment == null)) return false;
		if (imgPath != null) update += " picture = ? , ";
		if (rating != null) update += " rating = ? , ";
		if (comment != null) update += " comment = ? , ";
		update= update.substring(0, update.length() - 2);
		update += " WHERE id = ? AND username = ? ";
		
		try {
			if (conn.isClosed()) connectToDb();
			PreparedStatement stmt = conn.prepareStatement(update);
			
			int paramcount = 1;
			if (imgPath != null) { stmt.setString(paramcount, imgPath); paramcount++; }
			if (rating != null) { stmt.setInt(paramcount, rating.intValue()); paramcount++; }
			if (comment != null) { stmt.setString(paramcount, comment); paramcount++; }
			stmt.setInt(paramcount, id);
			paramcount++;
			stmt.setString(paramcount, username);
			
			if (stmt.executeUpdate() == 0) { conn.close(); return false; }
			conn.commit();
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
}
