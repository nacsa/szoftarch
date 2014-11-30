package com.test.vaadintest.ui;

import org.vaadin.teemu.wizards.WizardStep;

import com.test.vaadintest.businesslogic.FieldUtil;
import com.vaadin.ui.Component;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;


public class InitWizardStep implements WizardStep{

	OptionGroup optionGroup;
	TextField priceField;
	TimeSelecter availFromField;
	TimeSelecter availUntilField;

	
	
	public InitWizardStep(OptionGroup optionGroup, TextField priceField, TimeSelecter availFromField, TimeSelecter availUntilField) {
		this.optionGroup = optionGroup;
		this.priceField = priceField;
		this.availFromField = availFromField;
		this.availUntilField = availUntilField;
	}
	
	@Override
	public String getCaption() {
		return "Init";
	}

	@Override
	public Component getContent() {
		VerticalLayout content = new VerticalLayout();
		content.setSpacing(true);
		content.setMargin(true);
		content.addComponent(optionGroup);
		
		content.addComponent(priceField);
		content.addComponent(availFromField);
		content.addComponent(availUntilField);
		
		return content;
	}

	@Override
	public boolean onAdvance() {
		boolean allow = true;
		if(FieldUtil.isFieldFilled(priceField)){
			if(!FieldUtil.isPositiveValid(priceField.getValue())){
				allow = false;
				ParkingNotification.show("Price field should be positive number");
			}
		}

		//nem feltétlenül muszáj ezeket kitölteni, a db jól kezeli
		if(FieldUtil.isFieldFilled(availFromField)){
			if ( ! FieldUtil.validateTimeFormat(availFromField.getValue())){
				allow = false;
				ParkingNotification.show("Time format should be HH:MM.");
			}
		}
		if(FieldUtil.isFieldFilled(availUntilField)){
			if ( ! FieldUtil.validateTimeFormat(availUntilField.getValue())){
				allow = false;
				ParkingNotification.show("Time format should be HH:MM.");
			}
		}
		return allow;
	}

	@Override
	public boolean onBack() {
		return false;
	}

}
