package com.hotel.booking.repository;

import com.hotel.booking.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

/**
 * HotelRepository handles all database operations for Hotel.
 * Extending JpaRepository<Hotel, Long> gives us free methods:
 *   - save(hotel)       → inserts or updates a hotel in the DB
 *   - findById(id)      → finds a hotel by its ID
 *   - findAll()         → returns all hotels
 *   - deleteById(id)    → deletes a hotel by ID
 * We only write extra methods for custom queries.
 */
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    // Search hotels by location — case-insensitive partial match.
    // e.g. searching "paris" will find "Paris, France"
    // Spring JPA reads the method name and automatically writes the SQL!
    List<Hotel> findByLocationContainingIgnoreCase(String location);

    // Custom JPQL query to get a list of all unique city/location names.
    // DISTINCT ensures no city appears twice.
    // WHY: Used to populate a location dropdown on the homepage.
    @Query("SELECT DISTINCT h.location FROM Hotel h")
    List<String> findDistinctLocations();
}
