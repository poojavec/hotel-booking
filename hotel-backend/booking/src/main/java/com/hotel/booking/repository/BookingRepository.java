package com.hotel.booking.repository;

import com.hotel.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;
import java.util.Optional;

// This interface handles all database operations for Booking.
// By extending JpaRepository, Spring automatically gives us methods like:
//   save(), findById(), findAll(), deleteById(), etc.
// We only need to add custom methods here.
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find all bookings for a specific user, sorted from newest to oldest.
    // Spring JPA reads the method name and writes the SQL query automatically!
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Find a booking by its confirmation code (e.g. "A3F8B2C1")
    // Returns an Optional — meaning the booking might not exist, so we handle that case.
    Optional<Booking> findByBookingConfirmationCode(String confirmationCode);

    /**
     * Counts overlapping "CONFIRMED" bookings for a specific room and date range.
     * Overlap condition:
     * (newCheckIn < existingCheckOut) AND (newCheckOut > existingCheckIn)
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.bookingStatus = 'CONFIRMED' " +
            "AND (:checkInDate < b.checkOutDate AND :checkOutDate > b.checkInDate)")
    long countOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("checkInDate") Date checkInDate,
            @Param("checkOutDate") Date checkOutDate
    );

    /**
     * Same as above, but excludes one specific booking ID (useful for updates).
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.bookingStatus = 'CONFIRMED' " +
            "AND b.id != :excludeBookingId " +
            "AND (:checkInDate < b.checkOutDate AND :checkOutDate > b.checkInDate)")
    long countOverlappingBookingsExcluding(
            @Param("roomId") Long roomId,
            @Param("checkInDate") Date checkInDate,
            @Param("checkOutDate") Date checkOutDate,
            @Param("excludeBookingId") Long excludeBookingId
    );
}
