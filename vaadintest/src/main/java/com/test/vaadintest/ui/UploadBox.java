package com.test.vaadintest.ui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.ProgressListener;
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

    // Name of the uploaded file
    String filename;
    
    //ProgressBar progress = new ProgressBar(0.0f);
    
    // Show uploaded file in this placeholder
    Image image = new Image("Uploaded Image");
    
    boolean validUpload;
    
    public UploadBox() {
        // Create the upload component and handle all its events
        Upload upload = new Upload("Upload the image here", null);
        upload.setReceiver(this);
       // upload.addProgressListener(this);
        upload.addFailedListener(this);
        upload.addSucceededListener(this);
        
        // Put the upload and image display in a panel
        Panel panel = new Panel("Parking place image");
        //panel.setWidth("400px");
        panel.setSizeFull();
        VerticalLayout panelContent = new VerticalLayout();
        panelContent.setSpacing(true);
        panel.setContent(panelContent);
        panelContent.addComponent(upload);
        //panelContent.addComponent(progress);
        panelContent.addComponent(image);
        
        //progress.setVisible(false);
        image.setVisible(false);
        validUpload = false;
        
        setCompositionRoot(panel);
    }            
    
    public OutputStream receiveUpload(String filename, String mimeType) {
        this.filename = filename;
        os.reset(); // Needed to allow re-uploading
        return os;
    }

    /*
    @Override
    public void updateProgress(long readBytes, long contentLength) {
        progress.setVisible(true);
        if (contentLength == -1)
            progress.setIndeterminate(true);
        else {
            progress.setIndeterminate(false);
            progress.setValue(((float)readBytes) /
                              ((float)contentLength));
        }
    }
    */

    public void uploadSucceeded(SucceededEvent event) {
        image.setVisible(true);
       // image.setCaption("Uploaded Image " + filename +
       //         " has length " + os.toByteArray().length);
        
        // Display the image as a stream resource from
        // the memory buffer
        StreamSource source = new StreamSource() {
            private static final long serialVersionUID = -4905654404647215809L;

            public InputStream getStream() {
                return new ByteArrayInputStream(os.toByteArray());
            }
        };
        
        if (image.getSource() == null)
            // Create a new stream resource
            image.setSource(new StreamResource(source, filename));
        else { // Reuse the old resource
            StreamResource resource =
                    (StreamResource) image.getSource();
            resource.setStreamSource(source);
            resource.setFilename(filename);
        }

        image.markAsDirty();
        validUpload = true;
    }

    @Override
    public void uploadFailed(FailedEvent event) {
        Notification.show("Upload failed",
                          Notification.Type.ERROR_MESSAGE);
        validUpload = false;
    }
    
    public boolean isUploadValid(){
    	return validUpload;
    }
    
    public BufferedImage getUploadedBufferedImage() {
    	byte[]imageInByte = os.toByteArray();
		
    	InputStream in = new ByteArrayInputStream(imageInByte);
		BufferedImage bImage;
    	
    	try{
		// convert byte array back to BufferedImage
    		bImage = ImageIO.read(in);
    	}catch(Exception e){
    		return null;
    	}
    	
    	return bImage;
    	
    }
}
///eddig

