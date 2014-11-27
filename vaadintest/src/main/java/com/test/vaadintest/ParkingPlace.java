package com.test.vaadintest;

import java.awt.image.*;
import java.util.ArrayList;

public class ParkingPlace {
	
	String user;
	float lat;
	float lon;
	String address;
	float price;
	String availfrom;
	String availuntil;
	
	//azért legyenek vektorok, mert ha listázni akarjuk egy parkolóról az összeset valamelyikből,
	//akkor azt felvehessük egy ParkingPlace objektumba. De amikor hozzáadunk egy új parkolót és adunk meg képet/ratinget/kommentet, 
	//akkor mindegyikből csak egyet adhatunk meg. 
	ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
	ArrayList<Integer> ratings = new ArrayList<Integer>();
	ArrayList<String> comments = new ArrayList<String>();
	ArrayList<String> ratingusers = new ArrayList<String>();
	private int id;
	
	//a tagváltozók elérhetők, a létrehozás után be lehet állítani (http://en.wikipedia.org/wiki/Builder_pattern, csak nincs külön builder :D)
	// használható, mert a username-en kívül a többi menző lehet null.
	public ParkingPlace(String user){
		this.user = user;
		this.lat = 0;
		this.lon = 0;
		this.address = null;
		this.price = 0;
		this.availfrom = null;
		this.availuntil = null;
		this.id = 0;
	}
	
	//TODO: több konstruktor, hogy ne kelljen minden paramétert megadni. Pl.: ha csak a cím van meg, a GoogleMapstól lekérhetné a GPS-t
	public ParkingPlace(String user, float lat, float lon, String address, float price, String availfrom, String availuntil) {
		super();
		this.user = user;
		this.lat = lat;
		this.lon = lon;
		this.address = address;
		this.price = price;
		this.availfrom = availfrom;
		this.availuntil = availuntil;
		this.id = 0;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId() throws Exception{
		//id-jét csak akkor állítjuk be, ha frissen lett hozzáadva vagy úgy választottuk ki
		if (id == 0) throw new Exception("Nincs ID megadva!");
		else return id;
	}
	
	
	public float getLat(){
		return lat;
	}
	
	public float getLon(){
		return lon;
	}
	
	/**
	 * Mindegyik paraméter lehet null is.
	 * @param img
	 * @param rating
	 * @param comment
	 */
	public void addImgRatingComment(BufferedImage img, Integer rating, String comment, String user){
		imgs.add(img);
		ratings.add(rating);
		comments.add(comment);
		ratingusers.add(user);
	}
	
}
