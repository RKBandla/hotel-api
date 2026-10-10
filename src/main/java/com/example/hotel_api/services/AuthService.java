package com.example.hotel_api.services;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.hotel_api.exception.BusinessRuleException;
import com.example.hotel_api.exception.ConflictException;
import com.example.hotel_api.exception.UnauthorizedException;
import com.example.hotel_api.models.AppUser;
import com.example.hotel_api.models.AuthRequest;
import com.example.hotel_api.models.Guest;
import com.example.hotel_api.models.RegisterRequest;
import com.example.hotel_api.models.Role;
import com.example.hotel_api.repos.GuestRepository;
import com.example.hotel_api.repos.UserRepository;
import com.example.hotel_api.security.JwtUtil;

@Service
public class AuthService {

    public static final String ADMIN_USERNAME = "admin";

    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, GuestRepository guestRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.guestRepository = guestRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Registration always creates a GUEST (never an admin) plus their Guest profile.
    public void register(RegisterRequest request) {
        String username = request.getUsername().trim();
        if (username.equalsIgnoreCase(ADMIN_USERNAME)) {
            throw new BusinessRuleException("The username 'admin' is reserved");
        }
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username " + username + " already exists");
        }

        Guest guest = new Guest();
        guest.setFirstName(request.getFirstName().trim());
        guest.setLastName(request.getLastName().trim());
        guest.setEmail(request.getEmail().trim());
        Guest savedGuest = guestRepository.save(guest);

        AppUser user = new AppUser(username, passwordEncoder.encode(request.getPassword()),
                Role.GUEST, savedGuest.getId());
        userRepository.save(user);
    }

    // Returns token + role + guestId; wrong username/password -> 401
    public Map<String, Object> login(AuthRequest request) {
        AppUser user = userRepository.findByUsername(request.getUsername().trim()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }
        if (user.getRole() == null) {
            // users created in Step 3 have no role
            throw new UnauthorizedException("This account was created before roles existed - please register a new account");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", jwtUtil.generateToken(user));
        result.put("tokenType", "Bearer");
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("guestId", user.getGuestId());
        return result;
    }
}