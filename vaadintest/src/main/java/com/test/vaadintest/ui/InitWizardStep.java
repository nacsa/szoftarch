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
	TextField availFromField;
	TextField availUntilField;

	private Pattern pattern;
	private Matcher matcher;
	private static final String TIME24HOURS_PATTERN = 
            "([01]?[0-9]|2[0-3]):[0-5][0-9]";
	
	public InitWizardStep(OptionGroup optionGroup, TextField priceField, TextField availFromField, TextField availUntilField) {
		this.optionGroup = optionGroup;
		this.priceField = priceField;
		this.availFromField = availFromField;
		this.availUntilField = availUntilField;
		
        pattern = Pattern.compile(TIME24HOURS_PATTERN);
	}
	
	@Override
	public String getCaption() {
		return "Init";
	}

	@Override
	public Component getContent() {
		VerticalLayout content = new VerticalLayout();
		
		content.addComponent(optionGroup);
		
		content.addComponent(priceField);
		content.addComponent(availFromField);
		content.addComponent(availUntilField);
		
		return content;
	}

	public boolean validateTimeFormat(final String time){
		 
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

		//nem feltétlenül muszáj ezeket kitölteni, a db jól kezeli
		if(availFromField.getValue() == null || availFromField.getValue().equals("")){
			allow = false;
			Notification.show("Time interval is not set!");
		}
		else {
			if ( ! validateTimeFormat(availFromField.getValue())){ 
				Notification.show("Time format should be HH:MM.");
				allow = false;
			}
		}
		
		if(availUntilField.getValue() == null || availUntilField.getValue().equals("")){
			allow = false;
			Notification.show("Time interval is not set!");
		}
		else {
			if ( ! validateTimeFormat(availUntilField.getValue())){ 
				Notification.show("Time format should be HH:MM.");
				allow = false;
			}
		}
		
		return allow;
	}

	@Override
	public boolean onBack() {
		// TODO Auto-generated method stub
		return false;
	}

}
