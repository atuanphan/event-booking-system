package com.jonet.eventbooking.customexception;

public class RedisKeyNotFoundException extends RuntimeException{
    public RedisKeyNotFoundException(String message) {
        super(message);
    }
}
