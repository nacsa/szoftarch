package com.test.vaadintest.ui;

import org.vaadin.teemu.wizards.WizardStep;

import com.test.vaadintest.LocationUtil;
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
	Label addressLabel;
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
		HorizontalLayout layout2 = new HorizontalLayout();
		optionGroup = optionGroup_;
		content = new VerticalLayout();
		this.addressField = addressField_;
		addressLabel = new Label("Address: ");
		outLatLon = latlon;
		mapLatLon = new LatLon();
		
		
		setLocCalled = false;
		
		
		layout2.addComponent(addressLabel);
		layout2.addComponent(addressField);
		
		topMap = new GoogleMap(null, null, null);
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
		bottomMap.setHeight(600, Unit.PIXELS);
		bottomMap.setWidth(600, Unit.PIXELS);
		
		setLocButton = new Button("Set Location");
		setLocButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				handleSetLocButtonClick();
			}
		});
		
		innerTopLayout.addComponent(topMap);
		innerTopLayout.addComponent(layout2);
		innerTopLayout.addComponent(setLocButton);
		
		
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
			addressLabel.setVisible(true);
			
			topMap.setVisible(false);
			
		}else if (optionMap.equals(value)){
			addressField.setVisible(false);
			addressLabel.setVisible(false);
			
			topMap.setVisible(true);
			
			topMap.setCenter(bpCenter);
			topMap.setZoom(bpZoom);
			
		}else{ //optionLocation.equals(value)
			addressField.setVisible(false);
			addressLabel.setVisible(false);
			
			topMap.setVisible(false);

		}
		setLocCalled = false;
		
	}
	
	private void handleSetLocButtonClick(){
		String value = (String)optionGroup.getValue();
		LatLon tmpLatLon;
		
		bottomMap.clearMarkers();
		
		if(optionAddress.equals(value)){
			tmpLatLon = LocationUtil.getLatlonFromAddress(addressField.getValue());
		}else if (optionMap.equals(value)){
			tmpLatLon = mapLatLon;
			
		}else{ //optionLocation.equals(value)
			tmpLatLon = LocationUtil.getLatlonFromLocation();
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
			Notification.show("The location is not set!");
		}
		return setLocCalled;
	}

	@Override
	public boolean onBack() {

		return true;
	}

}
