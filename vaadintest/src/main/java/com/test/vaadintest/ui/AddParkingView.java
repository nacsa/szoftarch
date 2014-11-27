package com.test.vaadintest.ui;

import java.awt.image.BufferedImage;

import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.test.vaadintest.MyVaadinUI;
import com.test.vaadintest.ParkingPlace;
import com.vaadin.navigator.Navigator;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class AddParkingView extends BaseParkingView implements WizardProgressListener{

	TextField addressField;
	TextField priceField;
	TextField availFromField;
	TextField availUntilField;
	LatLon parkingLatLon;
	BufferedImage parkingImage;
	OptionGroup optionGroup;
	
	Wizard wizard;
	
	private static String optionAddress = "Use Address";
	private static String optionMap = "Use Map";
	private static String optionLocation = "Use Location";
	
	public AddParkingView(Navigator navigator) {
		super(navigator);
		name = "addparking";
		parkingLatLon = new LatLon();
		
		optionGroup = new OptionGroup();
		optionGroup.addItem(optionAddress);
		optionGroup.addItem(optionMap);
		optionGroup.addItem(optionLocation);
		optionGroup.select(optionAddress);
	
		addressField = new TextField("Address");
		priceField = new TextField("Price");
		availFromField = new TextField("Available from");
		availUntilField = new TextField("Available until");
		
		
		wizard = new Wizard();
		wizard.addStep(new InitWizardStep(optionGroup, priceField, availFromField, availUntilField));
		wizard.addStep(new LocationWizardStep(optionGroup, addressField, parkingLatLon));
		wizard.addStep(new ImageUploadWizardStep(parkingImage));
		wizard.addListener(this);
		
		
		midPanel.setContent(wizard);
		
		
		
	}
	
	
	

	@Override
	public void activeStepChanged(WizardStepActivationEvent event) {
		
	}

	@Override
	public void stepSetChanged(WizardStepSetChangedEvent event) {
		
	}

	@Override
	public void wizardCompleted(WizardCompletedEvent event) {
		String user = ((MyVaadinUI)UI.getCurrent()).getLoginedUserName();
		float lat = (float)parkingLatLon.getLat();
		float lon = (float)parkingLatLon.getLon();
		String address = addressField.getValue();
		float price = Float.parseFloat(priceField.getValue());
		String availfrom = availFromField.getValue();
		String availuntil = availUntilField.getValue();
		
		//valami ilyesmi:
		//db.add(new ParkingPlace(...) paraméterbe a fenti adatok, ID-t meg db-nek kellene generálni
		((MyVaadinUI)UI.getCurrent()).getDB().addParkingPlace(
				new ParkingPlace(user, lat, lon, address, price, availfrom, availuntil));
		endWizard("Parking place added! Have a nice day!");
	}

	@Override
	public void wizardCancelled(WizardCancelledEvent event) {
		endWizard("Parking place adding cancelled!");
	}
	
	
	private void endWizard(String message){
		Notification.show(message);
		
		//TODO: resetelni a wizardstep-eket
		navigator.navigateTo("");
		wizard.back();
		wizard.back();
	}
	
}
