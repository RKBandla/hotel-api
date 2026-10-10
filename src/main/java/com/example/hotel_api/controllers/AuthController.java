package com.example.hotel_api.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotel_api.models.AppUser;
import com.example.hotel_api.models.AuthRequest;
import com.example.hotel_api.services.AuthService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register  {"username":"rohan","password":"secret123"}
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody AuthRequest request) {
        AppUser user = authService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered", "username", user.getUsername()));
    }

    // POST /api/auth/login  {"username":"rohan","password":"secret123"}  -> token
    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody AuthRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return Map.of("token", token, "tokenType", "Bearer", "username", request.getUsername());
    }
}