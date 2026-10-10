package com.example.hotel_api.repos;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.hotel_api.models.Booking;

// Spring builds these queries from the method names:
//   findByRoomId -> { "roomId": ? }      findByGuestId -> { "guestId": ? }
public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByRoomId(String roomId);

    List<Booking> findByGuestId(String guestId);
}