package com.test.vaadintest.ui;

import org.w3c.dom.UserDataHandler;

import com.test.vaadintest.LoginUtil;
import com.test.vaadintest.MyVaadinUI;
import com.test.vaadintest.ParkingNotification;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class LoginView extends VerticalLayout implements View{

	Navigator navigator;
	TextField nameField;
	PasswordField pwField;
	
	public final static String name = "login";
	
	public LoginView(Navigator navigator) {
		this.navigator = navigator;
		

		setSizeFull();
		GridLayout gridLayout = new GridLayout(2, 3);
		gridLayout.setSpacing(true);
		gridLayout.setMargin(true);
		
		Panel panel = new Panel();
		panel.addStyleName("v-login-panel");
		
		Label nameLabel = new Label("Login name:");
		Label pwLabel = new Label("Password:");
		
		nameField = new TextField();
		pwField = new PasswordField();
		
		Button loginButton = new Button("Login");
		loginButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				login(nameField.getValue(), pwField.getValue());
			}
		});
		gridLayout.addComponent(nameLabel, 0, 0);
		gridLayout.addComponent(nameField, 1, 0);
		gridLayout.addComponent(pwLabel, 0, 1);
		gridLayout.addComponent(pwField, 1, 1);
		gridLayout.addComponent(loginButton, 1, 2);
	
		
		panel.setContent(gridLayout);
		panel.setWidth("350px");
		addComponent(panel);
		setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

	}
	
	
	public void login(String name, String password){
		boolean success = LoginUtil.successfulLogin(name, password);
		
		if(success){
			ParkingNotification.show("Succesfull login! " + name);
			((MyVaadinUI)UI.getCurrent()).setLoginedUserName(name);
			navigator.navigateTo("");
			
			//plusz elnavigálunk
		}else{
			ParkingNotification.show("Login failed! Invalid login name or password");
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {			
		nameField.setValue("");
		pwField.setValue("");
		
	}

}
