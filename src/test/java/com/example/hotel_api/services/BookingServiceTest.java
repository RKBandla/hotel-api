package com.example.hotel_api.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.hotel_api.exception.BusinessRuleException;
import com.example.hotel_api.exception.NotFoundException;
import com.example.hotel_api.models.Booking;
import com.example.hotel_api.models.BookingRequest;
import com.example.hotel_api.models.Guest;
import com.example.hotel_api.models.Room;
import com.example.hotel_api.models.RoomType;
import com.example.hotel_api.repos.BookingRepository;
import com.example.hotel_api.repos.GuestRepository;
import com.example.hotel_api.repos.RoomRepository;

class BookingServiceTest {

    // "today" is always 1 Dec 2026 in these tests
    private static final LocalDate TODAY = LocalDate.of(2026, 12, 1);

    private BookingService bookingService;
    private RoomService roomService;
    private Room room;
    private Guest guest;

    @BeforeEach
    void setUp() {
        RoomRepository roomRepository = new RoomRepository();
        GuestRepository guestRepository = new GuestRepository();
        BookingRepository bookingRepository = new BookingRepository();
        Clock clock = Clock.fixed(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

        bookingService = new BookingService(bookingRepository, roomRepository, guestRepository, clock);
        roomService = new RoomService(roomRepository, bookingRepository);

        room = new Room();
        room.setNumber("101");
        room.setType(RoomType.DOUBLE);
        room.setPricePerNight(new BigDecimal("120.00"));
        room.setCapacity(2);
        roomRepository.save(room);

        guest = new Guest();
        guest.setFirstName("Rohan");
        guest.setLastName("Bandla");
        guest.setEmail("rohan@example.com");
        guestRepository.save(guest);
    }

    // helper: booking from TODAY+fromDay to TODAY+toDay
    private BookingRequest request(int fromDay, int toDay) {
        BookingRequest r = new BookingRequest();
        r.setRoomId(room.getId());
        r.setGuestId(guest.getId());
        r.setCheckIn(TODAY.plusDays(fromDay));
        r.setCheckOut(TODAY.plusDays(toDay));
        return r;
    }

    @Test
    void bookingCalculatesTotalPrice() {
        Booking booking = bookingService.createBooking(request(1, 4));   // 3 nights x 120

        assertEquals(0, booking.getTotalPrice().compareTo(new BigDecimal("360.00")));
    }

    @Test
    void cannotDoubleBookOverlappingDates() {
        bookingService.createBooking(request(1, 4));

        assertThrows(BusinessRuleException.class, () -> bookingService.createBooking(request(3, 6)));
    }

    @Test
    void backToBackBookingsAreAllowed() {
        bookingService.createBooking(request(1, 4));

        assertNotNull(bookingService.createBooking(request(4, 6)).getId());
    }

    @Test
    void checkInCannotBeInThePast() {
        assertThrows(BusinessRuleException.class, () -> bookingService.createBooking(request(-1, 2)));
    }

    @Test
    void cancelledBookingFreesTheRoom() {
        Booking first = bookingService.createBooking(request(1, 4));
        bookingService.cancelBooking(first.getId());

        assertNotNull(bookingService.createBooking(request(2, 3)).getId());
        assertThrows(BusinessRuleException.class, () -> bookingService.cancelBooking(first.getId()));
    }

    @Test
    void availableRoomsExcludeBookedOnes() {
        bookingService.createBooking(request(1, 4));

        assertEquals(0, roomService.getAvailableRooms(TODAY.plusDays(2), TODAY.plusDays(3), null).size());
        assertEquals(1, roomService.getAvailableRooms(TODAY.plusDays(4), TODAY.plusDays(5), null).size());
    }

    @Test
    void unknownRoomGivesNotFound() {
        BookingRequest r = request(1, 2);
        r.setRoomId("999");

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(r));
    }
}