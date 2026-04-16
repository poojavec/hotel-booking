package com.hotel.booking.repository;

import com.hotel.booking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Date;
import java.util.Optional;
import java.util.List;

/**
 * RoomRepository handles all database operations for Room.
 * Extending JpaRepository gives us free CRUD methods.
 */
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Get all rooms belonging to a specific hotel.
    // Spring JPA reads "findByHotelId" and auto-generates: SELECT * FROM rooms WHERE hotel_id = ?
    List<Room> findByHotelId(Long hotelId);

    /**
     * This custom JPQL query finds rooms for a hotel that are NOT already booked
     * for the given date range.
     *
     * HOW IT WORKS:
     * It gets all rooms for the hotel, then EXCLUDES any room that has a CONFIRMED
     * booking that overlaps with our check-in and check-out dates.
     *
     * The overlap condition: a booking overlaps if it starts before our check-out
     * AND ends after our check-in. This catches all possible overlap scenarios.
     *
     * @param hotelId     - the hotel we are searching in
     * @param checkInDate - requested check-in date
     * @param checkOutDate - requested check-out date
     */
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId " +
            "AND r.id NOT IN (SELECT b.room.id FROM Booking b WHERE b.bookingStatus = 'CONFIRMED' " +
            "AND (b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate))")
    List<Room> findAvailableRoomsByHotelAndDate(
            @Param("hotelId") Long hotelId,
            @Param("checkInDate") Date checkInDate,
            @Param("checkOutDate") Date checkOutDate
    );

    /**
     * Fetch a room and lock the row in the database.
     * This prevents other transactions from modifying or booking this same room
     * until the current transaction is finished.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    Optional<Room> findByIdWithLock(@Param("id") Long id);
}
