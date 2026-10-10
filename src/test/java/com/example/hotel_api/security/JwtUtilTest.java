package com.example.hotel_api.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private static final String SECRET = "test-secret-0123456789abcdef0123456789";

    @Test
    void validTokenGivesBackTheUsername() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 60_000);
        String token = jwtUtil.generateToken("rohan");

        assertEquals("rohan", jwtUtil.extractUsername(token));
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 60_000);
        String token = jwtUtil.generateToken("rohan");
        String tampered = token.substring(0, token.length() - 2) + "xx";   // change the signature

        assertNull(jwtUtil.extractUsername(tampered));
    }

    @Test
    void tokenFromAnotherSecretIsRejected() {
        String token = new JwtUtil("another-secret-9876543210fedcba9876543210", 60_000).generateToken("rohan");

        assertNull(new JwtUtil(SECRET, 60_000).extractUsername(token));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, -1_000);   // already expired
        String token = jwtUtil.generateToken("rohan");

        assertNull(jwtUtil.extractUsername(token));
    }

    @Test
    void garbageIsRejected() {
        assertNull(new JwtUtil(SECRET, 60_000).extractUsername("not-a-token"));
    }
}