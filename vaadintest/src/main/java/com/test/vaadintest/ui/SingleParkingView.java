package com.test.vaadintest.ui;



import java.util.Iterator;

import org.vaadin.teemu.ratingstars.RatingStars;

import com.test.vaadintest.FieldUtil;
import com.test.vaadintest.LocationUtil;
import com.test.vaadintest.MyVaadinUI;
import com.test.vaadintest.ParkingPlace;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class SingleParkingView extends BaseParkingView{

	private ParkingPlace currentParkingPlace;
	Label tmpLabel;
	
	TextField addressField;
	TextField priceField;
	TextField availFromField;
	TextField availUntilField;
	GoogleMap map;
	RatingStars newRating;
	UploadBox newUploadBox;
	
	TextArea newCommentArea;
	Button modifyButton;
	Button saveButton;
	Button cancelButton;
	
	String savedAddress;
	String savedPrice;
	String savedAvailFrom;
	String savedAvailUntil;
	
	LatLon parkingPlaceLatLon;
	
	boolean viewMode; // false - csak nézés, true - szerkesztés
	
	
	public SingleParkingView(Navigator navigator) {
		super(navigator);
		name = "parkingplace";
		tmpLabel = new Label();
		midPanel.setContent(tmpLabel);
		viewMode = false;
		
		addressField = new TextField("Address");
		priceField = new TextField("Price (Ft)");
		availFromField = new TextField("Available from");
		availUntilField = new TextField("Available until");
		addressField = new TextField("Address");
		
		modifyButton = new Button("Modify");
		modifyButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				enableModifying();
			}
		});
		saveButton = new Button("Save");
		saveButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				saveFieldValues();
				
			}
		});
		
		cancelButton = new Button("Cancel");
		cancelButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				cancelFieldValues();
				
			}
		});
		
		map = new GoogleMap(null,null,null);
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);	
		int parkingPlaceId;
		try{
			parkingPlaceId = Integer.parseInt(event.getParameters());
		}catch(NumberFormatException nfe){
			setupInvalidIdView(event.getParameters());
			return;
		}
		
		// Ha nem létezik a parkolóhely
		if(!((MyVaadinUI)UI.getCurrent()).getDB().doParkingPLaceExist(parkingPlaceId)){
			setupInvalidIdView(event.getParameters());
			return;
		}
		
		
		
		
		tmpLabel.setValue("ID should be: " + event.getParameters());
		
		currentParkingPlace = ((MyVaadinUI)UI.getCurrent()).getDB().queryAllDataOfOneParkingPlace(parkingPlaceId, true);
		if(currentParkingPlace == null)
			System.out.println("No parking place returned!");
		setupValidIdView();
		
		addressField.setReadOnly(false);
		priceField.setReadOnly(false);
		availFromField.setReadOnly(false);
		availUntilField.setReadOnly(false);
		
		addressField.setValue(currentParkingPlace.getAddress());
		availFromField.setValue(currentParkingPlace.getAvailfrom());
		availUntilField.setValue(currentParkingPlace.getAvailuntil());
		priceField.setValue(new Float(currentParkingPlace.getPrice()).toString());
		
		addressField.setReadOnly(true);
		priceField.setReadOnly(true);
		availFromField.setReadOnly(true);
		availUntilField.setReadOnly(true);
		
		
	}
	
	
	private void setupInvalidIdView(String parameter){
		midPanel.setSizeFull();
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		Label label = new Label(""
				+ "<h1><b>404</b></h1>"
				+ "<h2>We are sorry but Parking place <i><u>'" + parameter +"'</u></i> "
				+ "doesn't exists.</h2>");
		
		label.setContentMode(ContentMode.HTML);
		layout.addComponent(label);
		midPanel.setContent(layout);
		System.out.println(UI.getCurrent().getPage().getLocation().getHost());
		System.out.println(UI.getCurrent().getPage().getLocation().getPath());
		System.out.println(UI.getCurrent().getPage().getLocation().getPort());
		
	}
	
	private void setupValidIdView(){
		TabSheet tabSheet = new TabSheet();
		tabSheet.addTab(getTab1(), "Parking place information");
		tabSheet.addTab(getTab2(), "Comment, rating, image upload");
		midPanel.setContent(tabSheet);
	}
	
	
	
	private Layout getTab1(){
		VerticalLayout tabLayout = new VerticalLayout();
		tabLayout.setSpacing(true);
		tabLayout.setSizeFull();
		
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setSizeFull();
		
		GridLayout filterFieldLayout = new GridLayout(2,2);
		filterFieldLayout.setSpacing(true);
		filterFieldLayout.setMargin(true);
		
		addressField.setReadOnly(true);
		priceField.setReadOnly(true);
		availFromField.setReadOnly(true);
		availUntilField.setReadOnly(true);
		saveButton.setVisible(false);
		cancelButton.setVisible(false);
		
		if(currentParkingPlace.getUser().equals(((MyVaadinUI)UI.getCurrent()).getLoginedUserName())){
			modifyButton.setVisible(true);
		}else{
			modifyButton.setVisible(false);
		}
		
		
		
		
		filterFieldLayout.addComponent(addressField, 0, 0);
		filterFieldLayout.addComponent(priceField, 1, 0);
		filterFieldLayout.addComponent(availFromField, 0, 1);
		filterFieldLayout.addComponent(availUntilField, 1, 1);
		topLayout.addComponent(filterFieldLayout);
		topLayout.addComponent(map);
		map.setHeight("300px");
		map.setWidth("300px");
		
		map.clearMarkers();
		LatLon parkingLatLon = new LatLon(currentParkingPlace.getLat(),currentParkingPlace.getLon());
		map.addMarker("", parkingLatLon, false, null);
		map.setCenter(parkingLatLon);
		map.setZoom(15);
		topLayout.setComponentAlignment(map, Alignment.BOTTOM_CENTER);
		
		VerticalLayout buttonLayout = new VerticalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(modifyButton);
		buttonLayout.addComponent(saveButton);
		buttonLayout.addComponent(cancelButton);
		
		
		topLayout.addComponent(buttonLayout);
		
		VerticalLayout bottomLayout = new VerticalLayout();
		bottomLayout.setSpacing(true);
		bottomLayout.setSizeFull();
		
		
		bottomLayout.addComponent(getCommentLayout());
		
		
		tabLayout.addComponent(topLayout);
		tabLayout.addComponent(bottomLayout);
		
		return tabLayout;
	}
	
	
	private Layout getTab2(){
		HorizontalLayout tabLayout = new HorizontalLayout();
		tabLayout.setSpacing(true);
		tabLayout.setMargin(true);
		tabLayout.setSizeFull();
		
		VerticalLayout leftTabLayout = new VerticalLayout();
		leftTabLayout.setSpacing(true);

		newCommentArea = new TextArea("Comment");
		newCommentArea.setWidth("100%");
		newRating = new RatingStars();
		//a captionöket lehet el kéne rejteni, de máshogy nem tom hogyan lehet kiszedni őket.
		newRating.setCaption("Rating");
		newUploadBox = new UploadBox();
		newUploadBox.setCaption("Picture");
		
		Button uploadButton = new Button("Activate");
		uploadButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				uploadOtherUserChanges();
			}
		});
		leftTabLayout.addComponent(newCommentArea);
		leftTabLayout.addComponent(newRating);
		leftTabLayout.addComponent(uploadButton);
		
		tabLayout.addComponent(leftTabLayout);
		tabLayout.setExpandRatio(leftTabLayout, 0.7f);
		tabLayout.addComponent(newUploadBox);
		tabLayout.setExpandRatio(newUploadBox, 0.3f);
		return tabLayout;
	}
	
	
	private Layout getCommentLayout(){
		
		VerticalLayout outerLayout = new VerticalLayout();
		outerLayout.setSizeFull();
		for(int i = 0; i < currentParkingPlace.getRatingusers().size(); i++){
			HorizontalLayout innerLayout = new HorizontalLayout();
			innerLayout.setSpacing(true);
			innerLayout.setMargin(true);
			innerLayout.setSizeFull();
			innerLayout.addStyleName("v-comment-layout");
			Label nameLabel = new Label("<b>" + currentParkingPlace.getRatingusers().get(i) +"</b>");
			nameLabel.setContentMode(ContentMode.HTML);
			Label commentLabel = new Label(currentParkingPlace.getComments().get(i));
			commentLabel.setContentMode(ContentMode.HTML);
			
			RatingStars rating = new RatingStars();
			rating.setValue(currentParkingPlace.getRatings().get(i).doubleValue());
			rating.setReadOnly(true);
			
			innerLayout.addComponent(nameLabel);
			innerLayout.addComponent(commentLabel);
			innerLayout.addComponent(rating);
			
			innerLayout.setExpandRatio(nameLabel, 0.2f);
			innerLayout.setExpandRatio(commentLabel, 0.6f);
			innerLayout.setExpandRatio(rating, 0.2f);
			
			innerLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_CENTER);
			innerLayout.setComponentAlignment(commentLabel, Alignment.MIDDLE_LEFT);
			innerLayout.setComponentAlignment(rating, Alignment.MIDDLE_CENTER);
			
			outerLayout.addComponent(innerLayout);
		
		}
		return outerLayout;
	}
	
	
	private void uploadOtherUserChanges(){
		ParkingPlace ppRating = new ParkingPlace(((MyVaadinUI)UI.getCurrent()).getLoginedUserName());
		//TODO: A kép elérési útvonalának hozzáadása
		//TODO: ha a usernek van már commentje, akkor módosítsunk! DB függvény készen van rá!
		ppRating.addImgRatingComment(null, newRating.getValue().intValue(), newCommentArea.getValue(), ppRating.getUser()); 
		try {
			ppRating.setId(currentParkingPlace.getId());
			((MyVaadinUI)UI.getCurrent()).getDB().addParkRating(ppRating);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//TODO: ha a usernek van már kommentje, akkor azt jelenítsük meg a Tabon módosításra
	//erre csináltam függgvényt: hasUserRatedThis -> olvasd el a kommentjét + castolgasd át őket a sorrend szerint ha nem nullák.
	private void enableModifying(){
		addressField.setReadOnly(false);
		priceField.setReadOnly(false);
		availFromField.setReadOnly(false);
		availUntilField.setReadOnly(false);
		
		savedAddress = addressField.getValue();
		savedPrice = priceField.getValue();
		savedAvailFrom = availFromField.getValue();
		savedAvailUntil = availUntilField.getValue();
		
		modifyButton.setVisible(false);
		saveButton.setVisible(true);
		cancelButton.setVisible(true);
	}
	
	
	private void saveFieldValues(){

		//TODO FIELD VALIDATION!
		String newaddress = null;
		float newlat = 0;
		float newlon = 0;
		if (FieldUtil.isFieldFilled(addressField)){
			newaddress = addressField.getValue();
			LatLon newlatlon = LocationUtil.getLatlonFromAddress(newaddress);
			newlat = (float) newlatlon.getLat();
			newlon = (float) newlatlon.getLon();
		}
		ParkingPlace modified = new ParkingPlace(currentParkingPlace.getUser(), newlat, newlon, newaddress, 
				Float.parseFloat(priceField.getValue()), availFromField.getValue(), availUntilField.getValue());
		try {
			modified.setId(currentParkingPlace.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//TODO: saját képét módosítja: DB.modifiyImageRatingCommentOfParkingPlace(id, user, imgPath, rating, comment);
		((MyVaadinUI) UI.getCurrent()).getDB().modifyDataOfParkingPlace(modified);
		setunmodified();
	}
	
	private void cancelFieldValues(){
		addressField.setValue(savedAddress);
		priceField.setValue(savedPrice);
		availFromField.setValue(savedAvailFrom);
		availUntilField.setValue(savedAvailUntil);
		
		setunmodified();
	}
	
	private void setunmodified(){
		addressField.setReadOnly(true);
		priceField.setReadOnly(true);
		availFromField.setReadOnly(true);
		availUntilField.setReadOnly(true);
		
		cancelButton.setVisible(false);
		saveButton.setVisible(false);
		modifyButton.setVisible(true);
	}

}
