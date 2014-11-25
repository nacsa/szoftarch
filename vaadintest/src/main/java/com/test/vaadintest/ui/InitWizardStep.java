package com.test.vaadintest.ui;

import org.vaadin.teemu.wizards.WizardStep;



import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;


public class InitWizardStep implements WizardStep{

	OptionGroup optionGroup;
	TextField priceField;
	TextField intervalField;
	
	private static String optionAddress = "Use Address";
	private static String optionMap = "Use Map";
	private static String optionLocation = "Use Location";
	
	public InitWizardStep(OptionGroup optionGroup, TextField priceField, TextField intervalField) {
		this.optionGroup = optionGroup;
		this.priceField = priceField;
		this.intervalField = intervalField;
	}
	
	@Override
	public String getCaption() {
		return "Init";
	}

	@Override
	public Component getContent() {
		VerticalLayout content = new VerticalLayout();
		
		GridLayout gridLayout = new GridLayout(3,4);
				
		content.addComponent(optionGroup);
		content.addComponent(gridLayout);
		
		gridLayout.addComponent(new Label("Price: "),0,0);
		gridLayout.addComponent(new Label("Interval: "),0,1);
		
		gridLayout.addComponent(priceField,1,0);
		gridLayout.addComponent(intervalField,1,1);
		
		return content;
	}

	@Override
	public boolean onAdvance() {
		boolean allow = true;
		if(priceField.getValue() == null || priceField.getValue().equals(""))
			allow = false;

		if(intervalField.getValue() == null || intervalField.getValue().equals(""))
			allow = false;
		
		if(!allow){
			Notification.show("The price or the time interval is not set!");
		}
		
		return allow;
	}

	@Override
	public boolean onBack() {
		// TODO Auto-generated method stub
		return false;
	}

}
