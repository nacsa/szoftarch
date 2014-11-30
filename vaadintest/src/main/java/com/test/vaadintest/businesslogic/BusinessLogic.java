package com.test.vaadintest.businesslogic;

import java.util.ArrayList;

import com.test.vaadintest.Database;
import com.test.vaadintest.ParkingPlace;
import com.vaadin.tapio.googlemaps.client.LatLon;

public class BusinessLogic {
	static Database db = new Database();
	
	public static boolean checkUserAndPassword(String name, String password) {
		return db.checkUserAndPassword(name, password);
	}

	public static boolean addUser(String username, String password) {
		return db.addUser(username, password);
	}

	public static boolean isUsernameAlreadyTaken(String name) {
		return db.isUsernameAlreadyTaken(name);
	}

	public static boolean doParkingPLaceExist(int id) {
		return db.doParkingPLaceExist(id);
	}

	public static ArrayList<Object> hasUserRatedThis(int id, String user) {
		return db.hasUserRatedThis(id, user);
	}

	public static boolean addParkingPlace(ParkingPlace pp) {
		return db.addParkingPlace(pp);
	}

	public static  void addParkRating(ParkingPlace pp) throws Exception {
		db.addParkRating(pp);
	}

	public static ArrayList<ParkingPlace> queryParkingPlace(LatLon around,
			float distanceInGeoSecs, float maxprice, String from, String until) {
		return db.queryParkingPlace(around, distanceInGeoSecs, maxprice, from,
				until);
	}

	public static ParkingPlace queryAllDataOfOneParkingPlace(int id, boolean ratings) {
		return db.queryAllDataOfOneParkingPlace(id, ratings);
	}

	public static boolean modifyDataOfParkingPlace(ParkingPlace pp) {
		return db.modifyDataOfParkingPlace(pp);
	}

	public static boolean modifyImgRatingCommentOfParkingPlace(int id,
			String username, String imgPath, Integer rating, String comment) {
		return db.modifyImgRatingCommentOfParkingPlace(id, username, imgPath,
				rating, comment);
	}
}
