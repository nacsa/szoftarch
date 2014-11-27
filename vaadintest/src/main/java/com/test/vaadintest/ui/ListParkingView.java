package com.test.vaadintest.ui;

import java.util.ArrayList;
import java.util.List;

import com.test.vaadintest.LocationUtil;
import com.test.vaadintest.MyVaadinUI;
import com.test.vaadintest.ParkingPlace;
import com.vaadin.navigator.Navigator;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapInfoWindow;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

public class ListParkingView extends BaseParkingView{

	TextField addressField;
	TextField distanceField;
	TextField priceField;
	TextField availFromField;
	TextField availUntilField;
	
	GoogleMap map;
	
	private static final float DEFAULT_DISTANCE_VALUE = 500f; //m-ben
	private static final double EARTH_RADIUS = 6372797.0; //m-ben
	
	public ListParkingView(Navigator navigator) {
		super(navigator);
		name = "listparking";
		VerticalLayout mainLayout = new VerticalLayout();
		HorizontalLayout filterLayout = new HorizontalLayout();
		GridLayout filterFieldLayout = new GridLayout(2,3);
		
		addressField = new TextField("Address");
		distanceField = new TextField("Max distance");
		priceField = new TextField("Max price");
		availFromField = new TextField("Available from (HH:MM)");
		availUntilField = new TextField("Available until (HH:MM)");
		
		filterFieldLayout.addComponent(addressField, 0, 0);
		filterFieldLayout.addComponent(distanceField, 1, 0);
		filterFieldLayout.addComponent(priceField, 0, 1);
		filterFieldLayout.addComponent(availFromField, 0, 2);
		filterFieldLayout.addComponent(availUntilField, 1, 2);
		
	
		Button filterButton = new Button("Filter",new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				filterParkings();
			}
		});
		
		filterLayout.addComponent(filterFieldLayout);
		filterLayout.addComponent(filterButton);
		filterLayout.setComponentAlignment(filterButton, Alignment.BOTTOM_RIGHT);
		
		map = new GoogleMap(null,null,null);
		map.setSizeFull();
		map.setHeight("500px");
		Panel filterPanel = new Panel();
		filterPanel.setContent(filterLayout);
		
		mainLayout.addComponent(filterPanel);
		mainLayout.addComponent(map);
		mainLayout.setSizeFull();
		
		midPanel.setContent(mainLayout);
		
	}
	
	private boolean isFieldFilled(TextField field){
		return field != null && !"".equals(field.getValue());
	}
	
	private void filterParkings(){
		map.clearMarkers();
		

		//TODO fieldeket validálni kell
		
		String address = addressField.getValue();
		String distanceStr = distanceField.getValue();
		float distance;
		if(isFieldFilled(distanceField))
			distance = Float.parseFloat(distanceStr);
		else 
			distance = DEFAULT_DISTANCE_VALUE;
		float maxprice;
		if (isFieldFilled(priceField))
			maxprice = Float.parseFloat(priceField.getValue());
		else
			maxprice = 0;
		String availfrom;
		if (isFieldFilled(availFromField))
			availfrom = availFromField.getValue();
		else
			availfrom = null;
		String availuntil;
		if (isFieldFilled(availUntilField))
			availuntil = availUntilField.getValue();
		else
			availuntil = null;
		
		LatLon addresLatlon = null;
		float distanceInGeoSecs = 0;
		if (isFieldFilled(addressField)){
			addresLatlon = LocationUtil.getLatlonFromAddress(address);
		}
		else{
			addresLatlon = null;
			distanceField.setValue(""); //ilyenkor a distance-ot nem használjuk és ezt jelezzük a usernek is
		}
		
		ArrayList<ParkingPlace> filteredParkings = 
				((MyVaadinUI)UI.getCurrent()).getDB().queryParkingPlace(addresLatlon, distanceInGeoSecs, maxprice, availfrom, availuntil);
		
		//csak dummy megoldás a megejelenítéshez
		/*List<ParkingPlace> filteredParkings = new ArrayList<ParkingPlace>();
		ParkingPlace tmpPlace = new ParkingPlace("aaadz", 47.4805856f, 19.1303031f, "Budapest", 0f, "10:00", "18:00");
		tmpPlace.setId(10);
		filteredParkings.add(tmpPlace); */
		
		// ez már éles innentől
		for(ParkingPlace place : filteredParkings){
			if(isFieldFilled(addressField)){//ha filterelni kell address szerint
				
				if(distance > getLatLonDistance(new LatLon(place.getLat(), place.getLon()), addresLatlon)){
					addParkingMarkerToMap(place);
				}
			}else{
				addParkingMarkerToMap(place);
			}
		}
		
		
		
	}
	
	
	private void addParkingMarkerToMap(ParkingPlace place) {
		LatLon placeLatLon = new LatLon(place.getLat(), place.getLon());
		GoogleMapMarker marker = new GoogleMapMarker("parking", placeLatLon, false);
		String infoWindowContent="";
		try {
			infoWindowContent = "Content: <br/> "
			+ "ID: "+place.getId() + "<br/>" 
			+ "ide az id alapján a href-es mókával már linkelhetünk mindenfélét, "
			+ "és úgy el lehet érni a majdani részletes képet";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		GoogleMapInfoWindow infoWindow = new GoogleMapInfoWindow(infoWindowContent, marker);
		infoWindow.setWidth("300px");
		infoWindow.setHeight("200px");
		
		map.addMarker(marker);
		map.addMarkerClickListener( new OpenInfoWindowMarkerClickListener(map, marker, infoWindow));
		map.setCenter(placeLatLon);
		map.setZoom(15);
	}
	
	private float getLatLonDistance(LatLon place1, LatLon place2){
		double dlat = place2.getLat() - place1.getLat();
		double dlon = place2.getLon() - place1.getLon();
		
		double a = Math.pow(Math.sin(dlat/2.0), 2.0) + Math.cos(place1.getLat()) * Math.cos(place2.getLat()) * Math.pow(Math.sin(dlon/2.0), 2.0); 
		
		double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0-a));
		
		return (float) (EARTH_RADIUS * c);
	}
	
	
	
	public class OpenInfoWindowMarkerClickListener implements MarkerClickListener{
		private final GoogleMap map;
		private final GoogleMapMarker marker;
		private final GoogleMapInfoWindow infoWindow;
		
		public OpenInfoWindowMarkerClickListener(GoogleMap map,
				GoogleMapMarker marker, GoogleMapInfoWindow infoWindow) {
			super();
			this.map = map;
			this.marker = marker;
			this.infoWindow = infoWindow;
		}




		@Override
		public void markerClicked(GoogleMapMarker clickedMarker) {
			if(clickedMarker.equals(marker))
				map.openInfoWindow(infoWindow);
			
		}
		
	}
	
	
}
