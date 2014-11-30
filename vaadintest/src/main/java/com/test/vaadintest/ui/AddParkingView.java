package com.test.vaadintest.ui;


import org.vaadin.teemu.ratingstars.RatingStars;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.test.vaadintest.MyVaadinUI;
import com.test.vaadintest.ParkingPlace;
import com.test.vaadintest.businesslogic.BusinessLogic;
import com.test.vaadintest.businesslogic.FieldUtil;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class AddParkingView extends BaseParkingView implements WizardProgressListener{

	TextField addressField;
	TextField priceField;
	TimeSelecter availFromField;
	TimeSelecter availUntilField;
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
	
	public AddParkingView(Navigator navigator) {
		super(navigator);
		name = "addparking";
		parkingLatLon = new LatLon();
		
		optionGroup = new OptionGroup();
		optionGroup.addItem(optionAddress);
		optionGroup.addItem(optionMap);
		optionGroup.select(optionAddress);
	
		addressField = new TextField("Address");
		priceField = new TextField("Price (HUF/h)");
		availFromField = new TimeSelecter("Available from");
		availUntilField = new TimeSelecter("Available until");
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
		
		navigateAfterLogout = true;
				
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		if(((MyVaadinUI)UI.getCurrent()).getLoginedUserName() == null){
			Label label = new Label("<h3>Only logged in users can use this function</h3>");
			label.setContentMode(ContentMode.HTML);
			midPanel.setContent(label);
		}else{
			midPanel.setContent(wizard);
		}
		
		
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
		float price; 
		if (!FieldUtil.isFieldFilled(priceField)) price = 0;
		else price = Float.parseFloat(priceField.getValue());
		String availfrom = availFromField.getValue();
		String availuntil = availUntilField.getValue();
		
		String imagepath = imageStep.getUploadedImagePath(); 
		String comment = commentArea.getValue();
		int rating = this.rating.getValue().intValue();
		
		ParkingPlace pp = new ParkingPlace(user, lat, lon, address, price, availfrom, availuntil);
		pp.addImgRatingComment(imagepath, rating, comment, user);
		BusinessLogic.addParkingPlace(pp);
		
		
		endWizard("Parking place added! Have a nice day!");
	}

	@Override
	public void wizardCancelled(WizardCancelledEvent event) {
		endWizard("Parking place adding cancelled!");
	}
	
	
	private void endWizard(String message){
		ParkingNotification.show(message);
		
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
