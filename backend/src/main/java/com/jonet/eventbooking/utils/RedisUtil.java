package com.jonet.eventbooking.utils;

public class RedisUtil {

    public static String getRefreshTokenKey(String token) {
        return "refresh-token:".concat(token);
    }
}
