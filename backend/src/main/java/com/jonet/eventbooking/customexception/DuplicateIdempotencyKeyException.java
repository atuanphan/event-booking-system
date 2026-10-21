package com.jonet.eventbooking.customexception;

public class DuplicateIdempotencyKeyException extends RuntimeException{
    public DuplicateIdempotencyKeyException(String message) {
        super(message);
    }
}
