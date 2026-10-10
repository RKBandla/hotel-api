package com.example.hotel_api.repos;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.hotel_api.models.AppUser;

public interface UserRepository extends MongoRepository<AppUser, String> {

    Optional<AppUser> findByUsername(String username);   // { username: ? }

    boolean existsByUsername(String username);
}