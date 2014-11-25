package com.test.vaadintest.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	private Pattern pattern;
	private Matcher matcher;
	private static final String TIME24HOURS_PATTERN = 
            "([01]?[0-9]|2[0-3]):[0-5][0-9]";
	
	public InitWizardStep(OptionGroup optionGroup, TextField priceField, TextField intervalField) {
		this.optionGroup = optionGroup;
		this.priceField = priceField;
		this.intervalField = intervalField;
		
        pattern = Pattern.compile(TIME24HOURS_PATTERN);
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

	public boolean validate(final String time){
		 
		  matcher = pattern.matcher(time);
		  return matcher.matches();
	  }
	
	@Override
	public boolean onAdvance() {
		boolean allow = true;
		if(priceField.getValue() == null || priceField.getValue().equals("")){
			Notification.show("The price is not set!");
			allow = false;
		}

		if(intervalField.getValue() == null || intervalField.getValue().equals("")){
			allow = false;
			Notification.show("Time interval is not set!");
		}
		else {
			if ( ! validate(intervalField.getValue())){ 
				Notification.show("Time format should be HH:MM.");
				allow = false;
			}
		}
		//TODO: FORMÁTUM ellenőrzés a másik fieldre is!!
		
		return allow;
	}

	@Override
	public boolean onBack() {
		// TODO Auto-generated method stub
		return false;
	}

}
