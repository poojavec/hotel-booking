package com.hotel.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

// UserDTO is what we send to the frontend when returning user info.
// We don't send the raw User entity because it contains the password hash.
// This DTO only has the safe fields we want the frontend to see.

@Data
// @JsonInclude(NON_NULL) means: when converting to JSON, skip any field that is null.
// This keeps the response clean and avoids sending "null" values.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;           // Unique user ID
    private String email;      // User's email address
    private String firstName;  // User's first name
    private String lastName;   // User's last name
    private String role;       // "ROLE_USER" or "ROLE_ADMIN"
    private List<BookingDTO> bookings; // List of this user's bookings (only included when needed)
}
