package com.test.vaadintest.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

public class HomeView extends BaseParkingView {

	public HomeView(Navigator navigator) {
		super(navigator);
		ThemeResource resource = new ThemeResource("img/parking-home.png");
		Image img = new Image("",resource);
		img.setSizeFull();
		midPanel.setContent(img);
		name = "";
	}

}
