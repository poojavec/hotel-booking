package com.hotel.booking.exception;

public class OurException extends RuntimeException {

    // Calls the parent RuntimeException constructor with our custom message
    public OurException(String message) {
        super(message);
    }
}
