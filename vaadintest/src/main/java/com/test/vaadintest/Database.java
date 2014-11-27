package com.test.vaadintest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

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
          		+ "picture BLOB,"
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
			conn.commit();
			
			if (!pp.imgs.isEmpty() || !pp.ratings.isEmpty() || !pp.comments.isEmpty()){
				String getmaxid = "SELECT MAX(id) FROM parking";
				Statement query = conn.createStatement();
				ResultSet maxid = query.executeQuery(getmaxid);
				conn.close(); //azert hogy lehessen addParkRatinget külön is hívni.
				pp.setId(maxid.getInt("id"));
				addParkRating(pp);
			}
			
			stmt.close();
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
	
	//TODO: UNTESTED!!
	public void addParkRating(ParkingPlace pp) throws Exception{
		String addrating = "INSERT INTO parkrating (id, username, picture, rating, comment)"
				+ "VALUES (?, ?, ?, ?, ?)";
		if (conn.isClosed()) connectToDb();
		PreparedStatement stmt = conn.prepareStatement(addrating);
		
		stmt.setInt(1, pp.getId());
		stmt.setString(2, pp.user);
		if (pp.imgs.get(0) != null){
			ImageInputStream img = ImageIO.createImageInputStream(pp.imgs.get(0));
			stmt.setBlob(3, (InputStream)img);
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
	 * Összegyűjti az összes parkolóhelyhez megadott képet, kommentet és véleményt.
	 * @param res
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 */
	private ArrayList<ParkingPlace> gatherImgRatingCommentById(ResultSet res) throws SQLException, IOException, Exception{
		ArrayList<ParkingPlace> ret = new ArrayList<ParkingPlace>();
		while (res.next()){
			ParkingPlace pp = new ParkingPlace(res.getString("username"), res.getFloat("lat"), 
					res.getFloat("lon"), res.getString("address"), res.getFloat("price"), res.getString("availfrom"), res.getString("availuntil"));
			pp.setId(res.getInt("id"));
			
			String ratings = "SELECT * FROM parkrating WHERE "
					+ "id = ? ";
			PreparedStatement stmt2 = conn.prepareStatement(ratings);
			stmt2.setInt(1, pp.getId());
			stmt2.setString(2, pp.user);
			ResultSet resrating = stmt2.executeQuery();
			
			while (resrating.next()){
				pp.addImgRatingComment(ImageIO.read( resrating.getBlob("picture").getBinaryStream() ), 
						resrating.getInt("rating"), resrating.getString("comment"), resrating.getString("username"));
			}
		}
		return ret;
	}
	
	/**
	 * @return Visszaadja az összes Parkolóhelyet, amely címében benne van az "address" string.
	 * Feltöli a parkolóhelyről mondott összes véleményt, képet, értékelést is.
	 */
	public ArrayList<ParkingPlace> queryParkingPlaceByAddress(String address){
		String query = "SELECT * FROM parking"
				+ " WHERE address LIKE ? ";
		ArrayList<ParkingPlace> ret = null;
		
		try {
			if (conn.isClosed()) connectToDb();
			PreparedStatement stmt = conn.prepareStatement(query);
			address = new String ("%").concat(address.concat(new String("%")));
			stmt.setString(1, address);
			
			ResultSet res = stmt.executeQuery();
			ret = (gatherImgRatingCommentById(res));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * Lekéri azokat a parkolóhelyeket, melyeknek az ára nem haladja meg a "maxprice" paramétert
	 * @param maxprice
	 * @return
	 */
	public ArrayList<ParkingPlace> queryParkingPlaceByPrice(float maxprice){
		String query = "SELECT * FROM parking "
				+ "WHERE price <= ? ";
		
		ArrayList<ParkingPlace> ret = null;
		
		try {
			if (conn.isClosed()) connectToDb();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setFloat(1, maxprice);
			
			ResultSet res = stmt.executeQuery();
			ret = (gatherImgRatingCommentById(res));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * Visszatér azokkal a parkolóhelyekkel, amelyeknek rendelkezésre állása tartalmazza a megadott intervallumot.
	 * Ha az egyik érték hiányzik a DBben, akkor azt mindenre teljesülőnek tekinti.
	 * @param from
	 * @param until
	 * @return
	 */
	public ArrayList<ParkingPlace> queryParkingPlaceByTime(String from, String until){
		String query = "SELECT * FROM parking "
				+ "WHERE "
				+ "(time(availfrom) <= time( ? ) OR availfrom IS NULL) AND "
				+ "(time(availuntil) >= time ( ? ) OR availuntil IS NULL)";
		
		ArrayList<ParkingPlace> ret = null;
		
		try {
			if (conn.isClosed()) connectToDb();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, from);
			stmt.setString(2, until);
			
			ResultSet res = stmt.executeQuery();
			ret = (gatherImgRatingCommentById(res));	
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
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
}
