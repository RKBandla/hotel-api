package com.example.hotel_api.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.hotel_api.models.AppUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    // Step 5: the role (and the guest's id) are put INSIDE the signed token,
    // so the server knows who you are and what you may do without a database lookup.
    public String generateToken(AppUser user) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs));
        if (user.getGuestId() != null) {
            builder.claim("guestId", user.getGuestId());
        }
        return builder.signWith(key).compact();
    }

    // Returns who the token belongs to, or null if it is invalid, expired or tampered with
    public AuthUser parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return new AuthUser(claims.getSubject(),
                    claims.get("role", String.class),
                    claims.get("guestId", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public String extractUsername(String token) {
        AuthUser user = parse(token);
        return user == null ? null : user.username();
    }
}