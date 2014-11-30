package com.test.vaadintest.businesslogic;

public class LoginUtil {

	
	public static boolean successfulLogin(String name, String password){
		if (BusinessLogic.checkUserAndPassword(name, password)) return true;
		else return false;
	}
	
	
	public static boolean isUserNameAlreadyTaken(String username){
		return BusinessLogic.isUsernameAlreadyTaken(username);
	}
}
