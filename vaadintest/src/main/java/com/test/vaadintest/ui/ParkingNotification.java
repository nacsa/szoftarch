package com.test.vaadintest.ui;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class ParkingNotification {
	
	public static void show(String message){
		Notification.show(message, Type.WARNING_MESSAGE);
	}
}
