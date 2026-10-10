package com.example.hotel_api.models;

public enum Role {
    ADMIN,   // hotel staff: manages rooms and guests, sees every booking
    GUEST    // a customer: books rooms for themselves, sees only their own bookings
}