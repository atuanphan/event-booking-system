package com.jonet.eventbooking.utils;

public class StringUtils {
	public static boolean checkString(String str) {
		if(str != null && !str.equals(""))
			return true;
		return false;
	}
}
