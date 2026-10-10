package com.example.hotel_api.exception;

// Logged in, but not allowed to touch this data (e.g. another guest's booking) -> 403
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}