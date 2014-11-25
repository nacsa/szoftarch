package com.test.vaadintest.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Label;

public class HomeView extends BaseParkingView {

	public HomeView(Navigator navigator) {
		super(navigator);
		Label label = new Label("Home");
		name = "";
		midPanel.setContent(label);
	}

}
