package com.hotel.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

// RoomDTO holds the room information we send to the frontend.
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Skip null fields when converting to JSON
public class RoomDTO {
    private Long id;              // Room's unique ID
    private String roomType;      // e.g. "Deluxe Suite", "Standard Room"
    private Double pricePerNight; // Price per night in USD
    private Integer capacity;     // Max number of guests
    private String description;   // Room description
    private String amenities;     // Comma-separated amenities
    private Boolean isAvailable;  // Whether the room is currently available
    private HotelDTO hotel;       // The hotel this room belongs to (included when needed)
}
