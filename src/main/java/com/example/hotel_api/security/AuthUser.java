package com.example.hotel_api.security;

// Who is making the current request (read from the JWT by JwtAuthFilter)
public record AuthUser(String username, String role, String guestId) {

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}