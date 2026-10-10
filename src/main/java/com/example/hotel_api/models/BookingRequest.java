package com.example.hotel_api.models;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Body for POST /api/bookings */
public class BookingRequest {

    @NotBlank(message = "roomId is required")
    private String roomId;

    // Step 5: no longer required here.
    // A GUEST always books for themselves (taken from the token); an ADMIN must send it.
    private String guestId;

    @NotNull(message = "checkIn is required (yyyy-MM-dd)")
    private LocalDate checkIn;

    @NotNull(message = "checkOut is required (yyyy-MM-dd)")
    private LocalDate checkOut;

    public BookingRequest() {
    }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }
}