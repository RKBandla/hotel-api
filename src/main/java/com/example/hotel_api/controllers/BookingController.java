package com.example.hotel_api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.hotel_api.models.Booking;
import com.example.hotel_api.models.BookingRequest;
import com.example.hotel_api.security.AuthUser;
import com.example.hotel_api.services.BookingService;

import jakarta.validation.Valid;

// Step 5: @AuthenticationPrincipal gives us the logged-in user that JwtAuthFilter read from the token.
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ADMIN: all bookings. GUEST: only their own.
    @GetMapping
    public List<Booking> getBookings(@AuthenticationPrincipal AuthUser user) {
        return bookingService.getBookingsFor(user);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@AuthenticationPrincipal AuthUser user, @PathVariable String id) {
        return bookingService.getBookingFor(user, id);
    }

    // GUEST: {"roomId":"...","checkIn":"2026-12-01","checkOut":"2026-12-03"}
    // ADMIN: same plus "guestId"
    @PostMapping
    public ResponseEntity<Booking> createBooking(@AuthenticationPrincipal AuthUser user,
                                                 @Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBookingFor(user, request));
    }

    // POST /api/bookings/{id}/cancel
    @PostMapping("/{id}/cancel")
    public Booking cancelBooking(@AuthenticationPrincipal AuthUser user, @PathVariable String id) {
        return bookingService.cancelBookingFor(user, id);
    }
}