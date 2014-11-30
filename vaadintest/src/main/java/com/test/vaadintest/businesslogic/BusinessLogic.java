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

	public static ArrayList<ParkingPlace> queryParkingPlace(String address,
			float distance, float maxprice, String from, String until) {
		
		LatLon addresLatlon = null;
		float distanceInGeoSecs = 0;
		if (address == null || "".equals(address)){
			addresLatlon = null;
			address = null;
		}
		else{
			addresLatlon = LocationUtil.getLatlonFromAddress(address);
			LatLon addresLatlonPlusOneSec =  new LatLon (addresLatlon.getLat(), addresLatlon.getLon());
			double d = (double)1.0/3600.0;
			addresLatlonPlusOneSec.setLat(addresLatlon.getLat() + d);
			addresLatlonPlusOneSec.setLon(addresLatlon.getLon() + d);
			float distanceOfOneGeoSecond = LocationUtil.getLatLonDistance(addresLatlon, addresLatlonPlusOneSec);
			distanceInGeoSecs = (float) (2.0 * distance / distanceOfOneGeoSecond);
		}
		
		/*
		 * Mi történik itt:
		 * Kiszámolom, hogy a megadott cím által meghatározott földrajzi koordinátáktól ha ellépünk
		 * egy másodpercet (észak keletre => felső bescslés), akkor az mekkora távolságot jelent a földfelszínen. 
		 * Ezt az adatot felhasználhatjuk, mint egy mértékegységet, hogy a felhasnzáló által megadott távolság
		 * hány ilyen egységet jelent. Az adatbázisban erre már lehet szűrni.
		 * MEGJEGYZÉS: most nem függ a getLatLonDisatnce függvény visszatérése attól, hogy milyen 
		 * szélességi/hosszúsági körön vagyunk. Ez a számítás akkor lenne igazi, ha a getLatLonDistance
		 * a google mapstól kérdezné le a légvonalbeli távolságot két pont között. Ebben az esetben viszont 
		 * nem is lenne szükség a memóriában történő szűrésre! szóval egyelőre ez csak azt eredményezi, 
		 * hogy nem kell túl sok adatbázisból visszaadott ParkingPlace-szel számolnunk.  
		 */

		ArrayList <ParkingPlace> estimatedFiltered = db.queryParkingPlace(addresLatlon, distanceInGeoSecs, maxprice, from, until);
		ArrayList<ParkingPlace> exactFiltered = new ArrayList<ParkingPlace>();
		
		if(addresLatlon != null){//ha filterelni kell address szerint
			for (ParkingPlace pp : estimatedFiltered){
				if(distance >= LocationUtil.getLatLonDistance(new LatLon(pp.getLat(), pp.getLon()), addresLatlon)){
					exactFiltered.add(pp);
				}
			}
			return exactFiltered;
		}
		else return estimatedFiltered;
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
