package com.hotel.booking.service;

import com.hotel.booking.dto.BookingDTO;
import com.hotel.booking.dto.ResponseDTO;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Room;
import com.hotel.booking.entity.User;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.repository.UserRepository;
import com.hotel.booking.exception.OurException;
import com.hotel.booking.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * BookingService handles all booking-related business logic.
 *
 * @Transactional means: if any step inside a method fails, ALL database changes
 * from that method are rolled back (cancelled). This keeps data consistent.
 */
@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository; // Database access for bookings

    @Autowired
    private RoomRepository roomRepository; // Database access for rooms

    @Autowired
    private UserRepository userRepository; // Database access for users

    @Autowired
    private EmailService emailService; // For sending confirmation emails

    /**
     * SAVE BOOKING: Books a room for a user.
     * Steps: Validate → find room/user → check availability → save → send email
     */
    public ResponseDTO saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        ResponseDTO response = new ResponseDTO();
        try {
            // Step 1: Basic validation — room ID and user ID must be provided
            if (roomId == null || userId == null) {
                throw new OurException("Room ID and User ID are required");
            }

            // Step 2: Check-in date must be in the future (can't book for yesterday!)
            if (bookingRequest.getCheckInDate().before(new Date())) {
                throw new OurException("Check-in date must be in the future");
            }

            // Step 3: Find the room and lock it in the database
            // This prevents double booking by making other concurrent requests wait
            Room room = roomRepository.findByIdWithLock(roomId)
                    .orElseThrow(() -> new OurException("Room Not Found"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new OurException("User Not Found"));

            // Step 4: Check if the room is available using a fast database query
            long overlappingBookings = bookingRepository.countOverlappingBookings(
                    roomId, bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()
            );

            if (overlappingBookings > 0) {
                response.setStatusCode(400);
                response.setMessage("Room is not available for the selected dates");
                return response;
            }

            // Step 6: Set the room and user on the booking object
            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);

            // Step 7: Generate a random 8-character confirmation code (e.g. "A3F8B2C1")
            // UUID.randomUUID() generates a unique ID, we take the first 8 chars
            String confirmationCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            bookingRequest.setBookingConfirmationCode(confirmationCode);

            // Step 8: Save the booking to the database
            bookingRepository.save(bookingRequest);

            // Step 9: Send a booking confirmation email to the user
            emailService.sendBookingConfirmationEmail(
                    user.getEmail(), user.getFirstName(), confirmationCode
            );

            // Step 10: Return success
            response.setStatusCode(200);
            response.setMessage("Booking successful");
            response.setBookingConfirmationCode(confirmationCode);

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving booking: " + e.getMessage());
        }
        return response;
    }

    /**
     * FIND BY CONFIRMATION CODE: Look up a booking using its short code.
     */
    public ResponseDTO findBookingByConfirmationCode(String confirmationCode) {
        ResponseDTO response = new ResponseDTO();
        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode)
                    .orElseThrow(() -> new OurException("Booking not found with code: " + confirmationCode));
            response.setStatusCode(200);
            response.setBooking(Utils.mapBookingEntityToBookingDTOPlusAll(booking));
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error finding booking: " + e.getMessage());
        }
        return response;
    }

    /**
     * GET USER BOOKING HISTORY: Returns all bookings made by a specific user.
     */
    public ResponseDTO getUserBookingHistory(Long userId) {
        ResponseDTO response = new ResponseDTO();
        try {
            // Get all bookings for this user (sorted newest first)
            List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);

            // Convert each Booking entity to a BookingDTO
            List<BookingDTO> bookingDTOs = new ArrayList<>();
            for (Booking booking : bookings) {
                bookingDTOs.add(Utils.mapBookingEntityToBookingDTOPlusAll(booking));
            }

            response.setStatusCode(200);
            response.setBookingList(bookingDTOs);
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving booking history: " + e.getMessage());
        }
        return response;
    }

    /**
     * CANCEL BOOKING: Marks a booking as CANCELLED (does not delete it from DB).
     * Also sends a cancellation email to the user.
     */
    public ResponseDTO cancelBooking(Long bookingId) {
        ResponseDTO response = new ResponseDTO();
        try {
            // Find the booking
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new OurException("Booking not found"));

            // Change its status to CANCELLED
            booking.setBookingStatus("CANCELLED");
            bookingRepository.save(booking); // Save the updated status

            // Send cancellation email
            emailService.sendCancellationEmail(
                    booking.getUser().getEmail(),
                    booking.getUser().getFirstName(),
                    booking.getBookingConfirmationCode()
            );

            response.setStatusCode(200);
            response.setMessage("Booking cancelled successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error cancelling booking: " + e.getMessage());
        }
        return response;
    }

    /**
     * GET ALL BOOKINGS: Returns every booking in the system (Admin only).
     */
    public ResponseDTO getAllBookings() {
        ResponseDTO response = new ResponseDTO();
        try {
            List<Booking> bookings = bookingRepository.findAll();

            // Convert each booking entity to a DTO
            List<BookingDTO> bookingDTOs = new ArrayList<>();
            for (Booking booking : bookings) {
                bookingDTOs.add(Utils.mapBookingEntityToBookingDTOPlusAll(booking));
            }

            response.setStatusCode(200);
            response.setBookingList(bookingDTOs);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving all bookings: " + e.getMessage());
        }
        return response;
    }

    /**
     * UPDATE BOOKING: Changes the check-in and check-out dates of a booking.
     */
    public ResponseDTO updateBooking(Long bookingId, Booking bookingRequest) {
        ResponseDTO response = new ResponseDTO();
        try {
            // Find the existing booking we want to update
            Booking existingBooking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new OurException("Booking not found"));

            // Step 2: Lock the room record to ensure no concurrent bookings happen during update
            roomRepository.findByIdWithLock(existingBooking.getRoom().getId())
                    .orElseThrow(() -> new OurException("Room Not Found"));

            // Step 3: Check if the new dates are available (excluding this current booking)
            long overlappingBookings = bookingRepository.countOverlappingBookingsExcluding(
                    existingBooking.getRoom().getId(),
                    bookingRequest.getCheckInDate(),
                    bookingRequest.getCheckOutDate(),
                    bookingId
            );

            if (overlappingBookings > 0) {
                response.setStatusCode(400);
                response.setMessage("Room is not available for the newly selected dates");
                return response;
            }

            // Update the dates and save
            existingBooking.setCheckInDate(bookingRequest.getCheckInDate());
            existingBooking.setCheckOutDate(bookingRequest.getCheckOutDate());
            bookingRepository.save(existingBooking);

            response.setStatusCode(200);
            response.setMessage("Booking updated successfully");
            response.setBooking(Utils.mapBookingEntityToBookingDTOPlusAll(existingBooking));
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating booking: " + e.getMessage());
        }
        return response;
    }

    // Note: Availability check now handled by countOverlappingBookings in repository for better performance and safety.
}
