package com.test.vaadintest.ui;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.ui.Component;

public class ImageUploadWizardStep implements WizardStep{
	
	UploadBox uploadBox;
	
	public ImageUploadWizardStep() {
		uploadBox = new UploadBox();
	}
	
	
	@Override
	public String getCaption() {
		
		return "Upload image";
	}

	@Override
	public Component getContent() {
		
		return uploadBox;
	}

	@Override
	public boolean onAdvance() {
		return true;
	}

	@Override
	public boolean onBack() {
		return true;
	}
	
	public String getUploadedImagePath(){
		if(!uploadBox.isUploadValid())
			return null;
		return uploadBox.getUploadedImagePath(); 
		
	}
	
	public void reset(){
		uploadBox = new UploadBox();
	}

}
