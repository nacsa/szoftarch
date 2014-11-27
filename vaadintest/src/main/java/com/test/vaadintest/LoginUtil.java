package com.test.vaadintest;

import com.vaadin.ui.UI;

public class LoginUtil {

	
	public static boolean successfulLogin(String name, String password){
		if (((MyVaadinUI)UI.getCurrent()).getDB().checkUserAndPassword(name, password)) return true;
		else return false;
	}
	
	
	public static boolean isUserNameAlreadyTaken(String username){
		return ((MyVaadinUI)UI.getCurrent()).getDB().isUsernameAlreadyTaken(username);
	}
}
