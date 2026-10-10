package com.example.hotel_api.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.hotel_api.exception.ConflictException;
import com.example.hotel_api.exception.UnauthorizedException;
import com.example.hotel_api.models.AppUser;
import com.example.hotel_api.repos.UserRepository;
import com.example.hotel_api.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Create a new account; the password is saved as a BCrypt hash
    public AppUser register(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username '" + username + "' is already taken");   // 409
        }
        return userRepository.save(new AppUser(username, passwordEncoder.encode(password)));
    }

    // Check username + password; if correct, return a JWT token
    public String login(String username, String password) {
        AppUser user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");                // 401
        }
        return jwtUtil.generateToken(username);
    }
}