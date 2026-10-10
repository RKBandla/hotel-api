package com.example.hotel_api.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.hotel_api.exception.BusinessRuleException;
import com.example.hotel_api.exception.NotFoundException;
import com.example.hotel_api.models.Booking;
import com.example.hotel_api.models.BookingRequest;
import com.example.hotel_api.models.Room;
import com.example.hotel_api.models.RoomType;
import com.example.hotel_api.repos.BookingRepository;
import com.example.hotel_api.repos.GuestRepository;
import com.example.hotel_api.repos.RoomRepository;

// Step 2: the repositories are now MongoDB interfaces, so the tests use
// Mockito "mocks" (fake repositories) instead of a real database.
class BookingServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 12, 1);

    private BookingService bookingService;
    private RoomService roomService;
    private Room room;
    private final List<Booking> savedBookings = new ArrayList<>();   // our fake "bookings" collection

    @BeforeEach
    void setUp() {
        RoomRepository roomRepository = mock(RoomRepository.class);
        GuestRepository guestRepository = mock(GuestRepository.class);
        BookingRepository bookingRepository = mock(BookingRepository.class);

        room = new Room();
        room.setId("r1");
        room.setNumber("101");
        room.setType(RoomType.DOUBLE);
        room.setPricePerNight(new BigDecimal("120.00"));
        room.setCapacity(2);

        // rooms: only "r1" exists
        when(roomRepository.findById(anyString()))
                .thenAnswer(inv -> "r1".equals(inv.getArgument(0)) ? Optional.of(room) : Optional.empty());
        when(roomRepository.findAll()).thenReturn(List.of(room));

        // guests: only "g1" exists
        when(guestRepository.existsById(anyString())).thenAnswer(inv -> "g1".equals(inv.getArgument(0)));

        // bookings: kept in the savedBookings list
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            if (b.getId() == null) {
                b.setId("b" + (savedBookings.size() + 1));
                savedBookings.add(b);
            }
            return b;
        });
        when(bookingRepository.findById(anyString())).thenAnswer(inv ->
                savedBookings.stream().filter(b -> b.getId().equals(inv.getArgument(0))).findFirst());
        when(bookingRepository.findByRoomId(anyString())).thenAnswer(inv ->
                savedBookings.stream().filter(b -> b.getRoomId().equals(inv.getArgument(0))).toList());

        Clock clock = Clock.fixed(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        bookingService = new BookingService(bookingRepository, roomRepository, guestRepository, clock);
        roomService = new RoomService(roomRepository, bookingRepository);
    }

    private BookingRequest request(int fromDay, int toDay) {
        BookingRequest r = new BookingRequest();
        r.setRoomId("r1");
        r.setGuestId("g1");
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