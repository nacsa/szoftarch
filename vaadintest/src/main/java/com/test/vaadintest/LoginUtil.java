package com.test.vaadintest;

import com.vaadin.ui.UI;

public class LoginUtil {

	
	//TODO: kitölteni értelmesen DV segítségével!
	public static boolean successfulLogin(String name, String password){
		if (((MyVaadinUI)UI.getCurrent()).getDB().checkUserAndPassword(name, password)) return true;
		else return false;
	}
}
