package com.example.hotel_api.exception;

/** -> HTTP 409 (something already exists) */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}