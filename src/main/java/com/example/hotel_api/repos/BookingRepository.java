package com.example.hotel_api.repos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.example.hotel_api.models.Booking;

@Repository
public class BookingRepository {

    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(bookings.get(id));
    }

    public List<Booking> findByRoomId(String roomId) {
        return bookings.values().stream().filter(b -> b.getRoomId().equals(roomId)).toList();
    }

    public List<Booking> findByGuestId(String guestId) {
        return bookings.values().stream().filter(b -> b.getGuestId().equals(guestId)).toList();
    }

    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(String.valueOf(idCounter.incrementAndGet()));
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }
}