package com.test.vaadintest;

import java.sql.*;

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
          		+ "user CHAR(20),"
          		+ "lat REAL,"
          		+ "lon REAL,"
          		+ "address TEXT"
          		+ ");";
          
          stmt.executeUpdate(users);
          stmt.executeUpdate(parking);
          stmt.close();
          conn.commit();
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.exit(0);
        }
        System.out.println("Opened database successfully");
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
		if (pp.user == "" || pp.user == null) pp.user = "Unknown";
		String addpark = "INSERT INTO parking (user, lat, lon, address)"
				+ "VALUES (?, ?, ?, ?)";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(addpark);
			stmt.setString(1, pp.user);
			stmt.setFloat(2, pp.lat);
			stmt.setFloat(3, pp.lon);
			stmt.setString(4, pp.address);
			stmt.execute();
			stmt.close();
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		System.out.println("Parking place added");
		return true;
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
