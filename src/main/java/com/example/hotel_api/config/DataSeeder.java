package com.example.hotel_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.hotel_api.models.AppUser;
import com.example.hotel_api.models.Role;
import com.example.hotel_api.repos.UserRepository;
import com.example.hotel_api.services.AuthService;

// Runs once at startup: creates the single ADMIN login if it does not exist yet.
// Nobody can register as admin - this is the only way an admin account is made.
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final boolean seedAdmin;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder,
                      @Value("${app.admin-password}") String adminPassword,
                      @Value("${app.seed-admin:true}") boolean seedAdmin) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
        this.seedAdmin = seedAdmin;
    }

    @Override
    public void run(String... args) {
        if (!seedAdmin) {
            return;
        }
        if (!userRepository.existsByUsername(AuthService.ADMIN_USERNAME)) {
            userRepository.save(new AppUser(AuthService.ADMIN_USERNAME,
                    passwordEncoder.encode(adminPassword), Role.ADMIN, null));
            log.info("Created the admin user");
        }
    }
}