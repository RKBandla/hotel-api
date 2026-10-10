package com.example.hotel_api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hotel_api.exception.BusinessRuleException;
import com.example.hotel_api.exception.NotFoundException;
import com.example.hotel_api.models.Booking;
import com.example.hotel_api.models.Guest;
import com.example.hotel_api.repos.BookingRepository;
import com.example.hotel_api.repos.GuestRepository;

@Service
public class GuestService {

    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public GuestService(GuestRepository guestRepository, BookingRepository bookingRepository) {
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    public Guest getGuestById(String id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Guest " + id + " not found"));
    }

    public Guest createGuest(Guest guest) {
        guest.setId(null);
        return guestRepository.save(guest);
    }

    public Guest updateGuest(String id, Guest changes) {
        Guest existing = getGuestById(id);
        existing.setFirstName(changes.getFirstName());
        existing.setLastName(changes.getLastName());
        existing.setEmail(changes.getEmail());
        return guestRepository.save(existing);
    }

    public void deleteGuest(String id) {
        getGuestById(id);
        boolean hasActiveBookings = bookingRepository.findByGuestId(id).stream().anyMatch(Booking::isActive);
        if (hasActiveBookings) {
            throw new BusinessRuleException("Guest " + id + " has active bookings and cannot be deleted");
        }
        guestRepository.deleteById(id);
    }
}