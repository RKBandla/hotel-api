package com.example.hotel_api.repos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.hotel_api.models.Room;

// Step 2: Spring Data MongoDB writes the code for us.
// findAll, findById, existsById, save, deleteById all come from MongoRepository.
public interface RoomRepository extends MongoRepository<Room, String> {
}