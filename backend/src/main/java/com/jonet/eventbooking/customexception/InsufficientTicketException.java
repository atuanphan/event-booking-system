package com.jonet.eventbooking.customexception;

public class InsufficientTicketException extends RuntimeException {
    public InsufficientTicketException(String message) {
        super(message);
    }
}
