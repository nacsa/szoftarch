package com.test.vaadintest.ui;

import org.vaadin.teemu.wizards.WizardStep;

import com.test.vaadintest.businesslogic.FieldUtil;
import com.test.vaadintest.businesslogic.LocationUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.MapClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class LocationWizardStep implements WizardStep{

	VerticalLayout content;
	OptionGroup optionGroup;
	TextField addressField;
	LatLon outLatLon;
	LatLon mapLatLon;
	GoogleMap topMap;
	GoogleMap bottomMap;
	Button setLocButton;
	boolean setLocCalled;
	
	private static LatLon bpCenter = new LatLon(47.4698, 19.04904);
	private static int bpZoom = 15;
	
	
	
	private static String optionAddress = "Use Address";
	private static String optionMap = "Use Map";
	private static String optionLocation = "Use Location";
	
	
	public LocationWizardStep(OptionGroup optionGroup_, TextField addressField_, LatLon latlon) {
		VerticalLayout innerTopLayout = new VerticalLayout();
		VerticalLayout innerBotLayout = new VerticalLayout();
		optionGroup = optionGroup_;
		content = new VerticalLayout();
		content.setSpacing(true);
		this.addressField = addressField_;
		outLatLon = latlon;
		mapLatLon = new LatLon();
		
		
		setLocCalled = false;
		
		
		topMap = new GoogleMap(null, null, null);
		topMap.setCaption("You can click as much as you want, only the last marker will count.");
		topMap.addMapClickListener(new MapClickListener() {
			
			@Override
			public void mapClicked(LatLon position) {
				mapLatLon = position;
				topMap.addMarker(null, position, false, null);
			}
		});
		
		topMap.setVisible(false);
		topMap.setHeight(600, Unit.PIXELS);
		topMap.setWidth(600, Unit.PIXELS);
		
		bottomMap = new GoogleMap(null,null,null);
		bottomMap.setCaption("Indicator map");
		bottomMap.setHeight(600, Unit.PIXELS);
		bottomMap.setWidth(600, Unit.PIXELS);
		
		

		bottomMap.setCenter(bpCenter);
		bottomMap.setZoom(bpZoom);
		topMap.setCenter(bpCenter);
		topMap.setZoom(bpZoom);
		
		
		setLocButton = new Button("Set Location");
		setLocButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				handleSetLocButtonClick();
			}
		});
		
		innerTopLayout.setSpacing(true);
		innerTopLayout.setMargin(true);
		innerTopLayout.addComponent(topMap);
		innerTopLayout.addComponent(addressField);
		innerTopLayout.addComponent(setLocButton);
		
		innerBotLayout.setMargin(true);
		innerBotLayout.addComponent(bottomMap);
		
		content.addComponent(innerTopLayout);
		content.addComponent(innerBotLayout);
		
		
		
		optionGroup.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				locationOptionGroupChanged(event);
				
			}
		});
		
		
	}
	
	private void locationOptionGroupChanged(ValueChangeEvent event){
		String value = (String)event.getProperty().getValue();

		if(optionAddress.equals(value)){
			addressField.setVisible(true);
			
			topMap.setVisible(false);
			
		}else {
			addressField.setVisible(false);
			
			topMap.setVisible(true);
			
			topMap.setCenter(bpCenter);
			topMap.setZoom(bpZoom);
			
		}
		setLocCalled = false;
		
	}
	
	private void handleSetLocButtonClick(){
		String value = (String)optionGroup.getValue();
		LatLon tmpLatLon;
		
		bottomMap.clearMarkers();
		
		if(optionAddress.equals(value)){
			
			if(!FieldUtil.isFieldFilled(addressField)){
				ParkingNotification.show("Please give an address!");
				return;
			}
			
			tmpLatLon = LocationUtil.getLatlonFromAddress(addressField.getValue());
		}else {
			tmpLatLon = mapLatLon;
			
		}
		
		outLatLon.setLat(tmpLatLon.getLat());
		outLatLon.setLon(tmpLatLon.getLon());
		
		bottomMap.addMarker(null, outLatLon, false, null);
		bottomMap.setCenter(outLatLon);
		bottomMap.setZoom(bpZoom);
		
		
		setLocCalled = true;
	}
	
	
	@Override
	public String getCaption() {
		return "Location";
	}

	@Override
	public Component getContent() {
		return content;
	}

	
	
	
	@Override
	public boolean onAdvance() {
		if(!setLocCalled){
			ParkingNotification.show("The location is not set!");
		}
		return setLocCalled;
	}

	@Override
	public boolean onBack() {

		return true;
	}
	
	public void reset(){
		topMap.clearMarkers();
		topMap.setCenter(bpCenter);
		topMap.setZoom(bpZoom);
		bottomMap.clearMarkers();
		bottomMap.setCenter(bpCenter);
		bottomMap.setZoom(bpZoom);
		setLocCalled = false;
	}

}
