package com.example.hotel_api.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hotel_api.exception.BusinessRuleException;
import com.example.hotel_api.exception.NotFoundException;
import com.example.hotel_api.models.Booking;
import com.example.hotel_api.models.Room;
import com.example.hotel_api.models.RoomType;
import com.example.hotel_api.repos.BookingRepository;
import com.example.hotel_api.repos.RoomRepository;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room " + id + " not found"));
    }

    public Room createRoom(Room room) {
        room.setId(null);
        return roomRepository.save(room);
    }

    public Room updateRoom(String id, Room changes) {
        Room existing = getRoomById(id);
        existing.setNumber(changes.getNumber());
        existing.setType(changes.getType());
        existing.setPricePerNight(changes.getPricePerNight());
        existing.setCapacity(changes.getCapacity());
        return roomRepository.save(existing);
    }

    public void deleteRoom(String id) {
        getRoomById(id);
        boolean hasActiveBookings = bookingRepository.findByRoomId(id).stream().anyMatch(Booking::isActive);
        if (hasActiveBookings) {
            throw new BusinessRuleException("Room " + id + " has active bookings and cannot be deleted");
        }
        roomRepository.deleteById(id);
    }

    /** Rooms with no active booking between checkIn and checkOut (optionally only one type). */
    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        if (!checkOut.isAfter(checkIn)) {
            throw new BusinessRuleException("checkOut must be after checkIn");
        }
        return roomRepository.findAll().stream()
                .filter(room -> type == null || room.getType() == type)
                .filter(room -> isFree(room.getId(), checkIn, checkOut))
                .toList();
    }

    boolean isFree(String roomId, LocalDate checkIn, LocalDate checkOut) {
        return bookingRepository.findByRoomId(roomId).stream()
                .noneMatch(b -> b.isActive() && b.overlaps(checkIn, checkOut));
    }
}