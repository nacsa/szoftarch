package com.test.vaadintest.ui;

import com.test.vaadintest.MyVaadinUI;
import com.test.vaadintest.businesslogic.BusinessLogic;
import com.test.vaadintest.businesslogic.LoginUtil;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class RegistrationView extends VerticalLayout implements View{

	Navigator navigator;
	TextField nameField;
	PasswordField pwField;
	PasswordField pwAgainField;
	
	public final static String name = "registration";
	
	public RegistrationView(Navigator navigator) {
		this.navigator = navigator;
		

		setSizeFull();
		GridLayout gridLayout = new GridLayout(2, 4);
		gridLayout.setSpacing(true);
		gridLayout.setMargin(true);
		
		Panel panel = new Panel();
		panel.addStyleName("v-login-panel");
		
		
		nameField = new TextField("Login name");
		pwField = new PasswordField("Password");
		pwAgainField = new PasswordField("Repeat password");
		
		Button registButton = new Button("Registration");
		registButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				regist(nameField.getValue(), pwField.getValue(), pwAgainField.getValue());
			}
		});
		gridLayout.addComponent(nameField, 0, 0);
		gridLayout.addComponent(pwField, 0, 1);
		gridLayout.addComponent(pwAgainField, 0, 2);
		
		gridLayout.addComponent(registButton, 1, 3);
	
		
		panel.setContent(gridLayout);
		panel.setWidth("350px");
		
		addComponent(panel);
		setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

	}
	
	
	public void regist(String name, String password, String repeatPassword){
		if(name == null || "".equals(name)){
			ParkingNotification.show("User name is empty. Please give one!");
			nameField.focus();
			return;
		}
		if(LoginUtil.isUserNameAlreadyTaken(name)){
			ParkingNotification.show("Sorry, username ("+name+") already taken.");
			nameField.focus();
			return;
		}
		
		if(password == null || "".equals(password)){
			ParkingNotification.show("Password field is empty. Please give a password!");
			resetAndFocusPassword();
			return;
		}
		
		if(!password.equals(repeatPassword)){
			ParkingNotification.show("Password reapeat failed. Try again!");
			resetAndFocusPassword();
			return;
		}
		
		if (BusinessLogic.addUser(name, password))
			{
				ParkingNotification.show("Successfull registration!");
				//login after registration
				((MyVaadinUI)UI.getCurrent()).setLoginedUserName(name);
			}
		else 
			ParkingNotification.show("Registration failed!");
		
		navigator.navigateTo("");
		
	}
	
	private void resetAndFocusPassword(){
		pwField.setValue("");
		pwAgainField.setValue("");
		pwField.focus();
	}
	
	@Override
	public void enter(ViewChangeEvent event) {			
		nameField.setValue("");
		pwField.setValue("");
		
	}

}