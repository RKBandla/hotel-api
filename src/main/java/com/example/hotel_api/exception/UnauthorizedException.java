package com.example.hotel_api.exception;

/** -> HTTP 401 (wrong username or password) */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}