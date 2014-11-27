package com.test.vaadintest.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;

public class SingleParkingView extends BaseParkingView{

	String parameterId;
	Label tmpLabel;
	
	
	public SingleParkingView(Navigator navigator) {
		super(navigator);
		name = "parkingplace";
		tmpLabel = new Label();
		midPanel.setContent(tmpLabel);
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		
		tmpLabel.setValue("ID should be: " + event.getParameters());
		
		//TODO ID alapján DB lekérdezéssel elérjük a szükséges ParkingPlace példányt
		// ezután feltöltjük a majdani mezőket
	}
	
	

}
