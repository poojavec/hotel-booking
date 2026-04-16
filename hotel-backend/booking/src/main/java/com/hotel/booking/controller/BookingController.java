package com.hotel.booking.controller;

import com.hotel.booking.dto.ResponseDTO;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * BookingController handles all booking-related HTTP requests.
 * All URLs start with /bookings (e.g. /bookings/book-room/...)
 */
@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService; // Handles booking business logic

    /**
     * POST /bookings/book-room/{roomId}/{userId}
     * Books a specific room for a specific user.
     *
     * @PreAuthorize = This endpoint requires the user to be logged in (ROLE_USER or ROLE_ADMIN).
     *                 WHY? Anonymous users shouldn't be able to make bookings.
     * @PathVariable = Reads the {roomId} and {userId} values from the URL path.
     * @RequestBody = Reads the booking details (check-in date, check-out date) from the request body.
     */
    @PostMapping("/book-room/{roomId}/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> saveBookings(
            @PathVariable Long roomId,
            @PathVariable Long userId,
            @RequestBody Booking bookingRequest) {
        ResponseDTO response = bookingService.saveBooking(roomId, userId, bookingRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * GET /bookings/get-by-confirmation-code/{confirmationCode}
     * Looks up a booking by its short confirmation code.
     * Restricted to Admin or the user who owns the booking.
     */
    @GetMapping("/get-by-confirmation-code/{confirmationCode}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseDTO> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        ResponseDTO response = bookingService.findBookingByConfirmationCode(confirmationCode);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * GET /bookings/user-bookings/{userId}
     * Returns all past bookings made by a specific user.
     * Restricted to ensure users only see their own history.
     */
    @GetMapping("/user-bookings/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseDTO> getUserBookingHistory(@PathVariable Long userId) {
        ResponseDTO response = bookingService.getUserBookingHistory(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * DELETE /bookings/cancel-booking/{bookingId}
     * Cancels a booking (changes its status to CANCELLED).
     * Both regular users and admin can cancel.
     */
    @DeleteMapping("/cancel-booking/{bookingId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseDTO> cancelBooking(@PathVariable Long bookingId) {
        ResponseDTO response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * PUT /bookings/update-booking/{bookingId}
     * Updates the dates of an existing booking.
     * PUT = HTTP method used when updating an existing resource.
     */
    @PutMapping("/update-booking/{bookingId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseDTO> updateBooking(
            @PathVariable Long bookingId,
            @RequestBody Booking bookingRequest) {
        ResponseDTO response = bookingService.updateBooking(bookingId, bookingRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * GET /bookings/all
     * Returns ALL bookings in the system. Admin only.
     * Used on the Admin → Manage Bookings page.
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> getAllBookings() {
        ResponseDTO response = bookingService.getAllBookings();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
