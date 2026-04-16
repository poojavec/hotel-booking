package com.hotel.booking.controller;

import com.hotel.booking.dto.ResponseDTO;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import java.util.Date;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * HotelController handles all hotel-related HTTP requests.
 * All URLs start with /hotels (e.g. /hotels/all, /hotels/{id})
 */
@RestController
@RequestMapping("/hotels")
@CrossOrigin(origins = "*")
public class HotelController {

    @Autowired
    private HotelService hotelService; // Service handles database logic

    /**
     * GET /hotels/all
     * Public endpoint — returns all hotels. No login required.
     */
    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAllHotels() {
        ResponseDTO response = hotelService.getAllHotels();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * GET /hotels/{id}
     * Returns a single hotel by its ID, including its rooms list.
     * Used on the Hotel Details page.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getHotelById(@PathVariable Long id) {
        ResponseDTO response = hotelService.getHotelById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * GET /hotels/search?location=Paris
     * Searches hotels by location (partial, case-insensitive).
     * @RequestParam = reads query parameter from URL: /hotels/search?location=Paris
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchHotels(@RequestParam String location) {
        ResponseDTO response = hotelService.getHotelsByLocation(location);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * GET /hotels/available-hotels-by-date-and-location?checkInDate=2026-05-01&checkOutDate=2026-05-05&location=Paris
     * Finds hotels with available rooms for the given date range and location.
     *
     * @DateTimeFormat(iso = DATE) = tells Spring how to parse the date from URL (format: YYYY-MM-DD)
     */
    @GetMapping("/available-hotels-by-date-and-location")
    public ResponseEntity<ResponseDTO> getAvailableHotelsByDateAndLocation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date checkOutDate,
            @RequestParam String location) {
        ResponseDTO response = hotelService.getAvailableHotelsByDateAndLocation(checkInDate, checkOutDate, location);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * POST /hotels/add
     * Adds a new hotel. Admin only — @PreAuthorize checks the JWT token's role.
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> addHotel(@RequestBody Hotel hotel) {
        ResponseDTO response = hotelService.addHotel(hotel);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * PUT /hotels/update/{id}
     * Updates an existing hotel's fields. Admin only.
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> updateHotel(@PathVariable Long id, @RequestBody Hotel hotel) {
        ResponseDTO response = hotelService.updateHotel(id, hotel);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * DELETE /hotels/{id}
     * Permanently deletes a hotel and all its rooms. Admin only.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> deleteHotel(@PathVariable Long id) {
        ResponseDTO response = hotelService.deleteHotel(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
