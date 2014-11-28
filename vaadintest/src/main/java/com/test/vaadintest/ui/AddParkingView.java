package com.test.vaadintest.ui;

import java.awt.image.BufferedImage;

import org.vaadin.teemu.ratingstars.RatingStars;
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
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class AddParkingView extends BaseParkingView implements WizardProgressListener{

	TextField addressField;
	TextField priceField;
	TextField availFromField;
	TextField availUntilField;
	LatLon parkingLatLon;
	OptionGroup optionGroup;
	TextArea commentArea;
	RatingStars rating;
	
	Wizard wizard;
	InitWizardStep initStep;
	LocationWizardStep locationStep;
	ImageUploadWizardStep imageStep;
	CommentRatingWizardStep commentStep;
	
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
		commentArea = new TextArea("Comment");
		rating = new RatingStars();
		
		initStep = new InitWizardStep(optionGroup, priceField, availFromField, availUntilField);
		locationStep = new LocationWizardStep(optionGroup, addressField, parkingLatLon);
		imageStep = new ImageUploadWizardStep();
		commentStep = new CommentRatingWizardStep(commentArea, rating);
		wizard = new Wizard();
		wizard.addStep(initStep);
		wizard.addStep(locationStep);
		wizard.addStep(imageStep);
		wizard.addStep(commentStep);
		
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
		
		BufferedImage image = imageStep.getLoadedImage();
		String comment = commentArea.getValue();
		int rating = this.rating.getValue().intValue();
		
		ParkingPlace pp = new ParkingPlace(user, lat, lon, address, price, availfrom, availuntil);
		pp.addImgRatingComment(image, rating, comment, user);
		((MyVaadinUI)UI.getCurrent()).getDB().addParkingPlace(pp);
		
		
		endWizard("Parking place added! Have a nice day!");
	}

	@Override
	public void wizardCancelled(WizardCancelledEvent event) {
		endWizard("Parking place adding cancelled!");
	}
	
	
	private void endWizard(String message){
		Notification.show(message);
		
		navigator.navigateTo("");
		wizard.back();
		wizard.back();
		wizard.back();
		wizard.back();
		
		optionGroup.setValue(optionAddress);
		addressField.setValue("");
		priceField.setValue("");
		availFromField.setValue("");
		availUntilField.setValue("");
		commentArea.setValue("");
		rating.setValue(0.0);
		locationStep.reset();
		imageStep.reset();
	}
	
}
