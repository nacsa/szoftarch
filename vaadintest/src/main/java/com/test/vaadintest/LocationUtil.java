package com.test.vaadintest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vaadin.tapio.googlemaps.client.LatLon;

public class LocationUtil {
	
	private static final double EARTH_RADIUS = 6372797.0; //m-ben
	
	public static LatLon getLatlonFromAddress(String address_){
		if(address_ == null || "".equals(address_)){
			return null;
		}
		
		String address = escapeStringForGMApi(address_);
		
		
		double latitude = 0;
        double longitude = 0;
		
		try{
			StringBuilder str = new StringBuilder(
	                "http://maps.google.com/maps/api/geocode/json?address=");
	        str.append(address.replaceAll(" ", "+"));
	        str.append("&sensor=false");
	
	        URL url = new URL(str.toString());
	        URLConnection urlc = url.openConnection();
	        BufferedReader bfr = new BufferedReader(new InputStreamReader(
	                urlc.getInputStream()));
	
	        String line;
	        final StringBuilder builder = new StringBuilder(2048);
	        builder.append("[");
	        while ((line = bfr.readLine()) != null) {
	            builder.append(line);
	        }
	        builder.append("]");
	        final JSONArray jsa = new JSONArray(builder.toString());
	        final JSONObject jo = (JSONObject) jsa.get(0);
	        JSONArray results = jo.getJSONArray("results");
	        JSONObject geometry = results.getJSONObject(0).getJSONObject(
	                "geometry");
	        JSONObject loc = geometry.getJSONObject("location");
	        latitude = loc.getDouble("lat");
	        longitude = loc.getDouble("lng");
		}catch(Exception e){
			//err
		}
		
		return new LatLon(latitude, longitude);
	}
	
	public static String escapeStringForGMApi(String input){
		String ret;
		ret = input.replace("á", "a");
		ret = ret.replace("ä", "a");
		ret = ret.replace("é", "e");
		ret = ret.replace("í", "i");
		ret = ret.replace("ő", "o");
		ret = ret.replace("ö", "o");
		ret = ret.replace("ó", "o");
		ret = ret.replace("ú", "u");
		ret = ret.replace("ü", "u");
		ret = ret.replace("ű", "u");
		return ret;
	}
	
	public static LatLon getLatlonFromLocation(){
		return new LatLon(0,0);
	}
	
	public static float getLatLonDistance(LatLon place1, LatLon place2){
		double dlat = (place2.getLat() - place1.getLat()) / 180.0 * Math.PI;
		double dlon = (place2.getLon() - place1.getLon()) / 180.0 * Math.PI;
		
		double a = Math.pow(Math.sin(dlat/2.0), 2.0) + 
				Math.cos(place1.getLat()/ 180.0 * Math.PI) * Math.cos(place2.getLat()/ 180.0 * Math.PI) * Math.pow(Math.sin(dlon/2.0), 2.0); 
		
		double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0-a));
		
		return (float) (EARTH_RADIUS * c);
	}
	
}
