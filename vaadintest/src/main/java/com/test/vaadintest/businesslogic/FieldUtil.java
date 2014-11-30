package com.test.vaadintest.businesslogic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.test.vaadintest.ui.TimeSelecter;
import com.vaadin.ui.TextField;

public class FieldUtil {

	private static final String TIME24HOURS_PATTERN = 
	        "([01]?[0-9]|2[0-3]):[0-5][0-9]";
	private static final String REAL_NUMBER =
			"[0-9]*(.|,)[0-9]*";
	private static final Pattern pattern = Pattern.compile(TIME24HOURS_PATTERN);
	private static Matcher matcher;
	
	public static boolean isFieldFilled(TextField field){
		return field != null && !"".equals(field.getValue());
	}
	
	public static boolean isFieldFilled(TimeSelecter field){
		return field != null && field.getValue() != null;
	}
	
	public static boolean validateTimeFormat(final String time){
		 
		  matcher = pattern.matcher(time);
		  return matcher.matches();
	  }
	
	public static boolean isPositiveValid(String price){
		if (price.matches(REAL_NUMBER)) {
			if (Float.parseFloat(price) > 0)
				return true;
			else
				return false;
		}
		else return false;
	}
}
