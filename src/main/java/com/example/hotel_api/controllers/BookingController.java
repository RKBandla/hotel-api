package com.example.hotel_api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hotel_api.models.Booking;
import com.example.hotel_api.models.BookingRequest;
import com.example.hotel_api.services.BookingService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable String id) {
        return bookingService.getBookingById(id);
    }

    // POST /api/bookings {"roomId":"1","guestId":"1","checkIn":"2026-12-01","checkOut":"2026-12-03"}
    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }

    // POST /api/bookings/{id}/cancel
    @PostMapping("/{id}/cancel")
    public Booking cancelBooking(@PathVariable String id) {
        return bookingService.cancelBooking(id);
    }
}