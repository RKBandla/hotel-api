package com.example.hotel_api.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hotel_api.models.AuthRequest;
import com.example.hotel_api.models.RegisterRequest;
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

    // POST /api/auth/register
    // {"username":"rohan","password":"secret123","firstName":"Rohan","lastName":"Bandla","email":"rohan@test.com"}
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        Map<String, String> body = new LinkedHashMap<>();
        body.put("message", "User registered");
        body.put("username", request.getUsername().trim());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    // POST /api/auth/login {"username":"rohan","password":"secret123"}
    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }
}