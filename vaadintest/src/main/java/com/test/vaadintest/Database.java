package com.test.vaadintest;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

public class Database {

	private Connection conn;
	/**
	 * Konstruktor az adatbázis betöltéséhez/létrehozásához.
	*/
	public Database(){
		try {
          Class.forName("org.sqlite.JDBC");
          conn = DriverManager.getConnection("jdbc:sqlite:park.db");
          conn.setAutoCommit(false);
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
          		+ "availability TEXT,"
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
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.exit(0);
        }
        System.out.println("Opened database successfully");
	}
	
	public String encryptPass(String password){
		try {
			MessageDigest cript = MessageDigest.getInstance("SHA-1");
			cript.reset();
			cript.update(password.getBytes("utf8"));
			return  new String(cript.digest(), "utf8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
			PreparedStatement stmt = conn.prepareStatement(adduser);
			stmt.setString(1, username);
			stmt.setString(2, password);
			stmt.execute();
			stmt.close();
			conn.commit();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		System.out.println("User added");
		return true;
	}
	
	public boolean addParkingPlace(ParkingPlace pp){
		String addpark = "INSERT INTO parking (username, lat, lon, address, price, availability)"
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		
		try {
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
			if (pp.avail != null)
				stmt.setString(6, pp.avail);
			stmt.execute();
			
			if (!pp.imgs.isEmpty() || !pp.ratings.isEmpty() || !pp.comments.isEmpty()){
				String getmaxid = "SELECT MAX(id) FROM parking";
				Statement query = conn.createStatement();
				ResultSet maxid = query.executeQuery(getmaxid);
				pp.setId(maxid.getInt("id"));
				addParkRating(pp);
			}
			
			stmt.close();
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.getMessage();
		}
		System.out.println("Parking place added");
		return true;
	}
	
	//TODO: UNTESTED!!
	public void addParkRating(ParkingPlace pp) throws Exception{
		String addrating = "INSERT INTO parkrating(id, username, picture, rating, comment)"
				+ "VALUES (?, ?, ?, ?, ?)";
		PreparedStatement stmt = conn.prepareStatement(addrating);
		
		stmt.setInt(1, pp.getId());
		stmt.setString(2, pp.user);
		if (pp.imgs.firstElement() != null){
			ImageInputStream img = ImageIO.createImageInputStream(pp.imgs.firstElement());
			stmt.setBlob(3, (InputStream)img);
		}
		if (pp.ratings.firstElement() != null){
			stmt.setInt(4, pp.ratings.firstElement());
		}
		if (pp.comments.firstElement() != null){
			stmt.setString(5, pp.comments.firstElement());
		}
	}
	
	/**
	 * TODO: hogyan defináljunk patternt 
	 * úgy hogy ne kelljen minden attribútumra külön függévnyt írni?
	 * @return
	 */
	public ParkingPlace queryParkingPlace(){
		return null;
	}
}
