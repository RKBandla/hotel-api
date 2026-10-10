package com.example.hotel_api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hotel_api.models.Booking;
import com.example.hotel_api.models.Guest;
import com.example.hotel_api.services.BookingService;
import com.example.hotel_api.services.GuestService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestService guestService;
    private final BookingService bookingService;

    @Autowired
    public GuestController(GuestService guestService, BookingService bookingService) {
        this.guestService = guestService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Guest> getAllGuests() {
        return guestService.getAllGuests();
    }

    @GetMapping("/{id}")
    public Guest getGuestById(@PathVariable String id) {
        return guestService.getGuestById(id);
    }

    @PostMapping
    public ResponseEntity<Guest> createGuest(@Valid @RequestBody Guest guest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.createGuest(guest));
    }

    @PutMapping("/{id}")
    public Guest updateGuest(@PathVariable String id, @Valid @RequestBody Guest guest) {
        return guestService.updateGuest(id, guest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable String id) {
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/guests/{id}/bookings
    @GetMapping("/{id}/bookings")
    public List<Booking> getGuestBookings(@PathVariable String id) {
        return bookingService.getBookingsForGuest(id);
    }
}