package com.test.vaadintest.ui;

import com.test.vaadintest.MyVaadinUI;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class BaseParkingView extends VerticalLayout implements View{
	
	protected Navigator navigator;
	boolean navigateAfterLogout;
	protected Panel topPanel;
	protected Panel midPanel;
	
	protected String name;

	protected MenuBar userBar;
	
	public BaseParkingView(Navigator _navigator) {
		navigator = _navigator;
		//setSizeFull();
		topPanel = new Panel();
		topPanel.setStyleName("toppanel");
		topPanel.setHeight(100, Unit.PIXELS);
		addComponent(topPanel);
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSizeFull();
		topPanel.setContent(topLayout);
		MenuBar navigationBar = new MenuBar();
		topLayout.addComponent(navigationBar);
		topLayout.setComponentAlignment(navigationBar, Alignment.BOTTOM_LEFT);
		
		userBar = new MenuBar();
		topLayout.addComponent(userBar);
		topLayout.setComponentAlignment(userBar, Alignment.TOP_RIGHT);
		
		MenuBar.Command homeCommand = new MenuBar.Command() {	
			@Override
			public void menuSelected(MenuItem selectedItem) {
				navigator.navigateTo("");
			}
		};		
		MenuItem homeMenu = navigationBar.addItem("Home", null, homeCommand);
		
		MenuBar.Command listCommand = new MenuBar.Command() {	
			@Override
			public void menuSelected(MenuItem selectedItem) {
				navigator.navigateTo("listparking");
			}
		};		
		MenuItem listParkingMenu = navigationBar.addItem("Parkings", null, listCommand);
		
		
		MenuBar.Command addCommand = new MenuBar.Command() {	
			@Override
			public void menuSelected(MenuItem selectedItem) {
				navigator.navigateTo("addparking");
			}
		};		
		MenuItem addParkingMenu = navigationBar.addItem("Add parking", null, addCommand);
		
		
		midPanel = new Panel();
		midPanel.setStyleName("midpanel");
		midPanel.setSizeFull();
		addComponent(midPanel);
		setComponentAlignment(midPanel, Alignment.TOP_CENTER);
		
		navigateAfterLogout = false;
		
	}
	
	@Override
	public void enter(ViewChangeEvent event) {

		userBar.removeItems();
		String user = ((MyVaadinUI)UI.getCurrent()).getLoginedUserName();
		if(user != null){
			MenuBar.Command logoutCommand = new MenuBar.Command() {	
				@Override
				public void menuSelected(MenuItem selectedItem) {
					logout();
				}
			};
			
			userBar.addItem(user, null,null);
			userBar.addItem("logout", null,logoutCommand);
		}else{
			
			MenuBar.Command registCommand = new MenuBar.Command() {	
				@Override
				public void menuSelected(MenuItem selectedItem) {
					navigator.navigateTo(RegistrationView.name);
				}
			};
			userBar.addItem("registration", null,registCommand);
			
			MenuBar.Command loginCommand = new MenuBar.Command() {	
				@Override
				public void menuSelected(MenuItem selectedItem) {
					navigator.navigateTo(LoginView.name);
				}
			};
			userBar.addItem("login", null,loginCommand);
			
		}
		
	}
	
	private void logout(){
		String user = ((MyVaadinUI)UI.getCurrent()).getLoginedUserName();
		if(user == null)
			return;
		
		((MyVaadinUI)UI.getCurrent()).setLoginedUserName(null);
		Notification.show("Successfull logout!");
		
		
		userBar.removeItems();
		MenuBar.Command registCommand = new MenuBar.Command() {	
			@Override
			public void menuSelected(MenuItem selectedItem) {
				navigator.navigateTo(RegistrationView.name);
			}
		};
		userBar.addItem("registration", null,registCommand);
		MenuBar.Command loginCommand = new MenuBar.Command() {	
			@Override
			public void menuSelected(MenuItem selectedItem) {
				navigator.navigateTo(LoginView.name);
			}
		};
		userBar.addItem("login", null,loginCommand);
		
		if(navigateAfterLogout){
			navigator.navigateTo("");
		}
	}
	
	
	public String getName(){
		return name;
	}

}
