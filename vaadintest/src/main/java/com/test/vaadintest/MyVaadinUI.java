package com.test.vaadintest;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
public class MyVaadinUI extends UI
{
	
	private Database db;
	private Map map;
	
    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "com.test.vaadintest.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        db = new Database();
        map = new Map();
        
        for (int i=0; i<5; i++){
        	ParkingPlace p = new ParkingPlace("balas", 23.4f, 14.2424f, "Budapest, 1117 Iriniyi J. 42.");
        	db.addParkingPlace(p);
        }
        
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

}
