package com.hotel.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// DTO = Data Transfer Object.
// This is a "wrapper" class we use to send data back to the frontend.
// Instead of sending raw database entities, we package data here cleanly.
// It holds all possible response data — we only fill in the fields that are relevant.

@Data
@Builder        // Allows creating this object using builder pattern: ResponseDTO.builder().message("ok").build()
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {

    // HTTP status code: 200 = success, 400 = bad request, 401 = unauthorized, 500 = server error
    private int statusCode;

    // A message to describe what happened (e.g. "Login successful", "Room not found")
    private String message;

    // JWT token returned after login — used for authentication in future requests
    private String token;

    // The user's role: "ROLE_USER" or "ROLE_ADMIN"
    private String role;

    // How long the JWT token is valid (e.g. "24H")
    private String expirationTime;

    // The confirmation code for a booking (e.g. "A3F8B2C1")
    private String bookingConfirmationCode;

    // ------ Single object responses ------
    private UserDTO user;       // Returned when getting a single user's info
    private HotelDTO hotel;     // Returned when getting a single hotel
    private RoomDTO room;       // Returned when getting a single room
    private BookingDTO booking; // Returned when getting a single booking

    // ------ List responses ------
    private List<UserDTO> userList;       // Returned when getting a list of users
    private List<HotelDTO> hotelList;     // Returned when getting a list of hotels
    private List<RoomDTO> roomList;       // Returned when getting a list of rooms
    private List<BookingDTO> bookingList; // Returned when getting a list of bookings
}
