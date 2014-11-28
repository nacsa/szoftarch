package com.test.vaadintest.ui;

import java.util.ArrayList;
import java.util.List;

import com.test.vaadintest.FieldUtil;
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
import com.vaadin.ui.Notification;
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
	
	public ListParkingView(Navigator navigator) {
		super(navigator);
		name = "listparking";
		VerticalLayout mainLayout = new VerticalLayout();
		HorizontalLayout filterLayout = new HorizontalLayout();
		GridLayout filterFieldLayout = new GridLayout(2,3);
		filterFieldLayout.setSpacing(true);
		
		addressField = new TextField("Address");
		distanceField = new TextField("Max distance (m)");
		priceField = new TextField("Max price");
		availFromField = new TextField("Available from (HH:MM)");
		availUntilField = new TextField("Available until (HH:MM)");
		
		filterFieldLayout.addComponent(addressField, 0, 0);
		filterFieldLayout.addComponent(distanceField, 1, 0);
		filterFieldLayout.addComponent(priceField, 0, 1);
		filterFieldLayout.addComponent(availFromField, 0, 2);
		filterFieldLayout.addComponent(availUntilField, 1, 2);
		filterFieldLayout.setMargin(true);
	
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
		mainLayout.setSpacing(true);
		
		midPanel.setContent(mainLayout);
		
	}
	
	private void filterParkings(){
		map.clearMarkers();
		
		String address = addressField.getValue();
		String distanceStr = distanceField.getValue();
		float distance = DEFAULT_DISTANCE_VALUE;
		if(FieldUtil.isFieldFilled(distanceField))
			if (FieldUtil.isPositiveValid(distanceField.getValue()))
				distance = Float.parseFloat(distanceStr);
		float maxprice = 0;
		if (FieldUtil.isFieldFilled(priceField))
			if (FieldUtil.isPositiveValid(priceField.getValue()))
				maxprice = Float.parseFloat(priceField.getValue());
			else Notification.show("Price format is not valid.");
		else
			maxprice = 0;
		String availfrom = null;
		if (FieldUtil.isFieldFilled(availFromField))
			if(FieldUtil.validateTimeFormat(availFromField.getValue()))
				availfrom = availFromField.getValue();
		String availuntil = null;
		if (FieldUtil.isFieldFilled(availUntilField))
			if(FieldUtil.validateTimeFormat(availFromField.getValue()))
				availuntil = availUntilField.getValue();		
		LatLon addresLatlon = null;
		float distanceInGeoSecs = 0;
		if (FieldUtil.isFieldFilled(addressField)){
			addresLatlon = LocationUtil.getLatlonFromAddress(address);
		}
		else{
			addresLatlon = null;
			distanceField.setValue(""); //ilyenkor a distance-ot nem használjuk és ezt jelezzük a usernek is
		}
		
		ArrayList<ParkingPlace> filteredParkings = 
				((MyVaadinUI)UI.getCurrent()).getDB().queryParkingPlace(addresLatlon, distanceInGeoSecs, maxprice, availfrom, availuntil);
		
		//TODO: A TÁVOLSÁG ALAPJÁN SZŰRÉST MÁR KITALÁLTAM, DE MÉG NINCS BENN, msot az összeset visszaadja a DB-ből.
		
		// ez már éles innentől
		for(ParkingPlace place : filteredParkings){
			if(FieldUtil.isFieldFilled(addressField)){//ha filterelni kell address szerint
				
				if(distance > LocationUtil.getLatLonDistance(new LatLon(place.getLat(), place.getLon()), addresLatlon)){
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
		
		GoogleMapInfoWindow infoWindow = new GoogleMapInfoWindow(infoWindowContent, marker);
		infoWindow.setWidth("300px");
		infoWindow.setHeight("200px");
		
		try {
			map.addMarker(marker);
			map.addMarkerClickListener( new OpenInfoWindowMarkerClickListener(map, marker, infoWindow, place.getId()));
			map.setCenter(placeLatLon);
			map.setZoom(15);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public class OpenInfoWindowMarkerClickListener implements MarkerClickListener{
		private final GoogleMap map;
		private final GoogleMapMarker marker;
		private final GoogleMapInfoWindow infoWindow;
		private final int idOfParkingPlace;
		
		public OpenInfoWindowMarkerClickListener(GoogleMap map,
				GoogleMapMarker marker, GoogleMapInfoWindow infoWindow, int idOParkingPlace) {
			super();
			this.map = map;
			this.marker = marker;
			this.infoWindow = infoWindow;
			this.idOfParkingPlace = idOParkingPlace;
		}

		@Override
		public void markerClicked(GoogleMapMarker clickedMarker) {
			if(clickedMarker.equals(marker)){
				map.openInfoWindow(infoWindow);
				try {
					ParkingPlace thispp = ((MyVaadinUI)UI.getCurrent()).getDB().
							queryAllDataOfOneParkingPlace(idOfParkingPlace, true);
					
					String infoWindowContent;
					String hostUrlString = "http://"+((MyVaadinUI)UI.getCurrent()).getHostUrl()
							+"#!parkingplace/"+thispp.getId();
					System.out.println(hostUrlString);
					
					if((thispp.getAvailfrom() != null && !"".equals(thispp.getAvailfrom())) &&
						(thispp.getAvailuntil() != null && !"".equals(thispp.getAvailuntil())))
					{
						infoWindowContent = 
								  "<h3>Parking Place</h3>" 
								+ "Address: " + thispp.getAddress() + "<br/>"
								+ "Price: " + thispp.getPrice() + "<br/>"
								+ "Available: " + thispp.getAvailfrom() +" - "+ thispp.getAvailuntil() + "<br/>"
								+ "Added by " + thispp.getUser() + "<br/><br/>"
								+ "<a href=\""+hostUrlString+"\">More information</a>";
					}else{
						infoWindowContent = 
								  "<h3>Parking Place</h3>" 
								+ "Address: " + thispp.getAddress() + "<br/>"
								+ "Price: " + thispp.getPrice() + "<br/>"
								+ "Added by " + thispp.getUser()
								+ "<br/><br/>"
								+ "<a href=\""+hostUrlString+"\">More information</a>";
					}
					
							
							
							
						
					infoWindow.setContent(infoWindowContent);
				} catch (Exception e) {
					e.printStackTrace();
				}					
			}
		}
		
	}
	
	
}
