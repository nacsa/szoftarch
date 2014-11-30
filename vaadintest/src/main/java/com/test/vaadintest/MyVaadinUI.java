package com.test.vaadintest;

import javax.servlet.annotation.WebServlet;

import com.test.vaadintest.ui.AddParkingView;
import com.test.vaadintest.ui.HomeView;
import com.test.vaadintest.ui.ListParkingView;
import com.test.vaadintest.ui.LoginView;
import com.test.vaadintest.ui.RegistrationView;
import com.test.vaadintest.ui.SingleParkingView;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
@PreserveOnRefresh
public class MyVaadinUI extends UI
{
	Navigator navigator;
    protected static final String MAINVIEW = "main";

    private String hostUrl;
	private String loginedUserName;
	


	@WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "com.test.vaadintest.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
    	getPage().setTitle("Parking places - find your place in the world");
        
        // Create a navigator to control the views
        navigator = new Navigator(this, this);
        loginedUserName = null;
        hostUrl = getPage().getLocation().getHost() + ":" + getPage().getLocation().getPort() + getPage().getLocation().getPath();
        // Create and register the views
        navigator.addView(LoginView.name, new LoginView(navigator));
        navigator.addView(RegistrationView.name, new RegistrationView(navigator));
        
        AddParkingView addview = new AddParkingView(navigator);
        navigator.addView(addview.getName(), addview);
        
        HomeView homeview = new HomeView(navigator);
        navigator.addView(homeview.getName(), homeview);
        
        ListParkingView listView = new ListParkingView(navigator);
        navigator.addView(listView.getName(), listView);
        
        SingleParkingView singleParkingView = new SingleParkingView(navigator);
        navigator.addView(singleParkingView.getName(), singleParkingView);
        
        final VerticalLayout layout = new VerticalLayout();
        
        layout.setMargin(true);
        setContent(layout);
        
    }
    
    public String getHostUrl(){
    	return hostUrl;
    }
    
    public String getLoginedUserName() {
		return loginedUserName;
	}

	public void setLoginedUserName(String loginedUserName) {
		this.loginedUserName = loginedUserName;
	}

}
