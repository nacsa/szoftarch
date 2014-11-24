package com.test.vaadintest;

import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;

public class Map {

	private GoogleMapMarker kakolaMarker = new GoogleMapMarker(
            "DRAGGABLE: Kakolan vankila", new LatLon(60.44291, 22.242415),
            true, null);
	public GoogleMap googleMap;
	
	public Map(){
		googleMap = new GoogleMap(null, null, null);
        googleMap.setCenter(new LatLon(60.440963, 22.25122));
        googleMap.setZoom(10);
        googleMap.setSizeFull();
        
        kakolaMarker.setAnimationEnabled(false);
        googleMap.addMarker(kakolaMarker);
       // googleMap.addMarker("DRAGGABLE: Paavo Nurmi Stadion", new LatLon(
        //        60.442423, 22.26044), true, "VAADIN/1377279006_stadium.png");
        
	}
}
