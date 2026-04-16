package com.hotel.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.Date;

// BookingDTO holds booking information we send back to the frontend.
// It includes room and user details nested inside for easy reading.
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Skip null fields when converting to JSON
public class BookingDTO {
    private Long id;                       // Booking's unique ID
    private Date checkInDate;              // Date the guest checks in
    private Date checkOutDate;             // Date the guest checks out
    private Double totalAmount;            // Total cost of the booking
    private String bookingStatus;          // "CONFIRMED" or "CANCELLED"
    private String bookingConfirmationCode; // Short unique code like "A3F8B2C1"
    private UserDTO user;                  // The user who made this booking
    private RoomDTO room;                  // The room that was booked
}
