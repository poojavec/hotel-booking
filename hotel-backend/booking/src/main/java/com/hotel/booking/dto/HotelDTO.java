package com.hotel.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

// HotelDTO holds the hotel information we send to the frontend.
// It mirrors the Hotel entity but is safe to expose via the API.
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Skip null fields in JSON output
public class HotelDTO {
    private Long id;             // Hotel's unique ID
    private String name;         // Hotel name
    private String location;     // Hotel location
    private String description;  // Hotel description
    private Double rating;       // Rating out of 5
    private String thumbnailUrl; // Image URL for the hotel
    private String amenities;    // Comma-separated amenities list
    private List<RoomDTO> rooms; // List of rooms in this hotel (included when needed)
}
