package com.example.hotel_api.exception;

/** -> HTTP 400 (a hotel rule was broken) */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}