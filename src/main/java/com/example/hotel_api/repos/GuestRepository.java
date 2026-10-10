package com.example.hotel_api.repos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.hotel_api.models.Guest;

public interface GuestRepository extends MongoRepository<Guest, String> {
}