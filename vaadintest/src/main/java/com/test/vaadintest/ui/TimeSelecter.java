package com.test.vaadintest.ui;

import com.test.vaadintest.businesslogic.FieldUtil;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;

public class TimeSelecter extends CustomComponent {
	
	private NativeSelect hourSelect;
	private NativeSelect minSelect;
	private Label separatorLabel;
	
	public TimeSelecter(String caption){
		this();
		this.setCaption(caption);
	}
	
	public TimeSelecter() {
		HorizontalLayout mainLayout = new HorizontalLayout();
		hourSelect = new NativeSelect();
		minSelect = new NativeSelect();
		
		
		for(int i = 0; i < 60; i++){
			if(i < 10)
				minSelect.addItem("0"+i);
			else
				minSelect.addItem(""+i);
		}
		
		for(int i = 0; i < 24; i++){
			if(i < 10)
				hourSelect.addItem("0"+i);
			else
				hourSelect.addItem(""+i);
		}
		
		separatorLabel = new Label(" : ");

		mainLayout.addComponent(hourSelect);
		mainLayout.addComponent(separatorLabel);
		mainLayout.addComponent(minSelect);
		
		setCompositionRoot(mainLayout);
	}
	
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		hourSelect.setVisible(visible);
		minSelect.setVisible(visible);
		separatorLabel.setVisible(visible);
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		hourSelect.setReadOnly(readOnly);
		minSelect.setReadOnly(readOnly);
	}
	
	public String getValue(){
		if(hourSelect.getValue() == null || minSelect.getValue() == null)
			return null;
		return hourSelect.getValue() + ":" + minSelect.getValue();
	}
	
	public void  setValue(String value){
		if(value == null){
			hourSelect.setValue(null);
			minSelect.setValue(null);
		}else{
			if(FieldUtil.validateTimeFormat(value)){
				String[] values = value.split(":");
				hourSelect.setValue(values[0]);
				minSelect.setValue(values[1]);
			}else{
				hourSelect.setValue(null);
				minSelect.setValue(null);
			}
		}
	}
}
