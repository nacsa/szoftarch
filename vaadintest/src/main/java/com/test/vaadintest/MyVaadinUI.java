package com.test.vaadintest;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.annotation.WebServlet;

import com.test.vaadintest.ui.AddParkingView;
import com.test.vaadintest.ui.HomeView;
import com.test.vaadintest.ui.LoginView;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
@PreserveOnRefresh
public class MyVaadinUI extends UI
{
	Navigator navigator;
    protected static final String MAINVIEW = "main";

	private Database db;
	private Map map;
	private String loginedUserName;
	


	@WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "com.test.vaadintest.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
    	getPage().setTitle("Navigation Example");
        
        // Create a navigator to control the views
        navigator = new Navigator(this, this);
        loginedUserName = null;
        
        // Create and register the views
        navigator.addView(LoginView.name, new LoginView(navigator));
        AddParkingView addview = new AddParkingView(navigator);
        navigator.addView(addview.getName(), addview);
        HomeView homeview = new HomeView(navigator);
        navigator.addView(homeview.getName(), homeview);
        
        final VerticalLayout layout = new VerticalLayout();
        //final HorizontalLayout layout = new HorizontalLayoutLayout();
        
        layout.setMargin(true);
        setContent(layout);

        db = new Database();
        map = new Map();
        
        layout.addComponent(map.googleMap);
        layout.setExpandRatio(map.googleMap, 1.0f);
        
        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                layout.addComponent(new Label("Thank you for clicking"));
                db.addUser("Balazs", "asdasd");
            }
        });
        layout.addComponent(button);
        
        
    }
    
    public Database getDB(){
    	return db;
    }
    
    public String getLoginedUserName() {
		return loginedUserName;
	}

	public void setLoginedUserName(String loginedUserName) {
		this.loginedUserName = loginedUserName;
	}

}
