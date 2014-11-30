package com.test.vaadintest.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;

import javax.activation.MimeType;

import com.vaadin.server.FileResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;


class UploadBox extends CustomComponent
      implements Receiver,
                 FailedListener, SucceededListener {
    private static final long serialVersionUID = -46336015006190050L;

    // Put upload in this memory buffer that grows automatically
    ByteArrayOutputStream os =
        new ByteArrayOutputStream(10240);

    public final static String UPLOADED_DIR_NAME = "img"+File.separator+"uploaded"+File.separator;
    
    // Name of the uploaded file
    String uploadedFileName;
    File file;
    
    // Show uploaded file in this placeholder
    Image image = new Image("Uploaded Image");
    
    boolean validUpload;
    
    public UploadBox() {
    	File uploads = new File(UPLOADED_DIR_NAME);
        if (!uploads.exists())
        	 uploads.mkdirs();
    	
        // Create the upload component and handle all its events
        Upload upload = new Upload("Upload the image here", null);
        upload.setReceiver(this);
        
        upload.addFailedListener(this);
        upload.addSucceededListener(this);
        
        // Put the upload and image display in a panel
        Panel panel = new Panel("Parking place image");
        panel.setSizeFull();
        VerticalLayout panelContent = new VerticalLayout();
        panelContent.setSpacing(true);
        panel.setContent(panelContent);
        panelContent.addComponent(upload);

        panelContent.addComponent(image);
        
        //progress.setVisible(false);
        image.setVisible(false);
        validUpload = false;
        
        setCompositionRoot(panel);
    }            
    
    public OutputStream receiveUpload(String filename, String mimeType) {
        
        FileOutputStream fos = null;
    	
        try {
        	
        	file = new File(UploadBox.UPLOADED_DIR_NAME+generateFileName(mimeType));
        	
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            
            return null;
        }
        return fos; // Return the output stream to write to
    }

    // Generate file name
    private String generateFileName(String mimeType){
    	uploadedFileName = System.currentTimeMillis()+"."+mimeType.split("/")[1];
    	return uploadedFileName;
    }

    public void uploadSucceeded(SucceededEvent event) {
    	
        image.setSource(new FileResource(file));
    	
    	image.setVisible(true);
    	image.addStyleName("v-uploadbox-image");

        image.markAsDirty();
        validUpload = true;
    }

    public void setPicture(String path){
    	file = new File(path);
    	
    	image.setSource(new FileResource(file));
    	
    	image.setVisible(true);
    	image.addStyleName("v-uploadbox-image");

        image.markAsDirty();
    }
    
    @Override
    public void uploadFailed(FailedEvent event) {
    	if(!("image/jpeg".equals(event.getMIMEType()) 
    			||"image/png".equals(event.getMIMEType())))
    	{
            ParkingNotification.show("Upload failed - Only upload jpg or png images");
    	}else{
            ParkingNotification.show("Upload failed");
    	}
    	
    	validUpload = false;
    }
    
    public boolean isUploadValid(){
    	return validUpload;
    }
    
    public String getUploadedImagePath() {
    	if(uploadedFileName == null)
    		return null;
    	return UPLOADED_DIR_NAME + uploadedFileName;
    }
}

