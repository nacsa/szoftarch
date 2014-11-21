package com.test.vaadintest;

public class ParkingPlace {
	
	String user;
	float lat;
	float lon;
	String address;
	float price;
	String avail;
	
	public ParkingPlace(String user, float lat, float lon, String address, float price, String avail) {
		super();
		this.user = user;
		this.lat = lat;
		this.lon = lon;
		this.address = address;
		this.price = price;
		this.avail = avail;
	}

}
