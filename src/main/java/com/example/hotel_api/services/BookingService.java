package com.example.hotel_api.services;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hotel_api.exception.BusinessRuleException;
import com.example.hotel_api.exception.ForbiddenException;
import com.example.hotel_api.exception.NotFoundException;
import com.example.hotel_api.models.Booking;
import com.example.hotel_api.models.BookingRequest;
import com.example.hotel_api.models.BookingStatus;
import com.example.hotel_api.models.Room;
import com.example.hotel_api.repos.BookingRepository;
import com.example.hotel_api.repos.GuestRepository;
import com.example.hotel_api.repos.RoomRepository;
import com.example.hotel_api.security.AuthUser;

@Service
public class BookingService {

    public static final int MAX_NIGHTS = 30;

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final Clock clock;

    @Autowired
    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository,
                          GuestRepository guestRepository, Clock clock) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.clock = clock;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking " + id + " not found"));
    }

    public List<Booking> getBookingsForGuest(String guestId) {
        if (!guestRepository.existsById(guestId)) {
            throw new NotFoundException("Guest " + guestId + " not found");
        }
        return bookingRepository.findByGuestId(guestId);
    }

    // synchronized: two people can't book the same room for the same night at the same moment
    public synchronized Booking createBooking(BookingRequest request) {
        // 1. room and guest must exist -> 404
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new NotFoundException("Room " + request.getRoomId() + " not found"));
        if (!guestRepository.existsById(request.getGuestId())) {
            throw new NotFoundException("Guest " + request.getGuestId() + " not found");
        }

        // 2. dates must make sense -> 400
        LocalDate checkIn = request.getCheckIn();
        LocalDate checkOut = request.getCheckOut();
        if (checkIn.isBefore(LocalDate.now(clock))) {
            throw new BusinessRuleException("checkIn cannot be in the past");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new BusinessRuleException("checkOut must be after checkIn");
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights > MAX_NIGHTS) {
            throw new BusinessRuleException("A booking can be at most " + MAX_NIGHTS + " nights");
        }

        // 3. no double booking: no ACTIVE booking of this room may overlap these dates -> 400
        boolean taken = bookingRepository.findByRoomId(room.getId()).stream()
                .anyMatch(b -> b.isActive() && b.overlaps(checkIn, checkOut));
        if (taken) {
            throw new BusinessRuleException("Room " + room.getNumber() + " is already booked for those dates");
        }

        // 4. create the booking with its total price
        Booking booking = new Booking();
        booking.setRoomId(room.getId());
        booking.setGuestId(request.getGuestId());
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setTotalPrice(room.getPricePerNight().multiply(BigDecimal.valueOf(nights)));
        return bookingRepository.save(booking);
    }

    public synchronized Booking cancelBooking(String id) {
        Booking booking = getBookingById(id);
        if (!booking.isActive()) {
            throw new BusinessRuleException("Booking " + id + " is already cancelled");
        }
        if (!LocalDate.now(clock).isBefore(booking.getCheckIn())) {
            throw new BusinessRuleException("Booking " + id + " has already started and cannot be cancelled");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    // ---------- Step 5: role-aware methods ----------
    // ADMIN can see and manage every booking. A GUEST only books for themselves
    // and can only see or cancel their own bookings.

    public List<Booking> getBookingsFor(AuthUser user) {
        if (user.isAdmin()) {
            return getAllBookings();
        }
        return bookingRepository.findByGuestId(user.guestId());
    }

    public Booking getBookingFor(AuthUser user, String id) {
        Booking booking = getBookingById(id);
        checkOwner(user, booking);
        return booking;
    }

    public Booking createBookingFor(AuthUser user, BookingRequest request) {
        if (user.isAdmin()) {
            if (request.getGuestId() == null || request.getGuestId().isBlank()) {
                throw new BusinessRuleException("guestId is required");
            }
        } else {
            request.setGuestId(user.guestId());   // a guest always books for themselves
        }
        return createBooking(request);
    }

    public Booking cancelBookingFor(AuthUser user, String id) {
        checkOwner(user, getBookingById(id));
        return cancelBooking(id);
    }

    private void checkOwner(AuthUser user, Booking booking) {
        if (!user.isAdmin() && !booking.getGuestId().equals(user.guestId())) {
            throw new ForbiddenException("You can only access your own bookings");
        }
    }
}