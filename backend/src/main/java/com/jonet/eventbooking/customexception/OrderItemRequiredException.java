package com.jonet.eventbooking.customexception;

public class OrderItemRequiredException extends RuntimeException{
    public OrderItemRequiredException(String message) {
        super(message);
    }
}
