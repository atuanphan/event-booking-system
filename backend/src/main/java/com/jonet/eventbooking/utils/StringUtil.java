package com.jonet.eventbooking.utils;

public class StringUtil {
    public static boolean hasText(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return true;
    }
}
