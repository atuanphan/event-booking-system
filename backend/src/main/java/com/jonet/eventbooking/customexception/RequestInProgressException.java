package com.jonet.eventbooking.customexception;

public class RequestInProgressException extends RuntimeException{
    public RequestInProgressException(String message) {
        super(message);
    }
}
