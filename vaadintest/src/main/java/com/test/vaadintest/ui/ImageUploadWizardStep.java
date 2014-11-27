package com.test.vaadintest.ui;

import java.awt.image.BufferedImage;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.ui.Component;

public class ImageUploadWizardStep implements WizardStep{
	
	UploadBox uploadBox;
	BufferedImage parkingImage;
	
	public ImageUploadWizardStep(BufferedImage parkingImage) {
		this.parkingImage = parkingImage;
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
		if(!uploadBox.isUploadValid())
			return false;
		
		parkingImage = uploadBox.getUploadedBufferedImage(); 
		return true;
	}

	@Override
	public boolean onBack() {
		return true;
	}

}
