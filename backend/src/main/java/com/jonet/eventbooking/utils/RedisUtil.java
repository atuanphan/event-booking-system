package com.jonet.eventbooking.utils;

import java.util.UUID;

public class RedisUtil {

    public static String getRefreshTokenKey(String token) {
        return "refresh-token:".concat(token);
    }

    public static String getOAuthCodeKey(String code) {
        return "oauth-code:" + code;
    }

    public static String ticketStockKey(UUID id) {
        return "ticket:stock:" + id;
    }
}
