package com.hotel.booking.controller;

import com.hotel.booking.dto.ResponseDTO;
import com.hotel.booking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

/**
 * RoomController handles all room-related HTTP requests.
 * All URLs start with /rooms (e.g. /rooms/add/{hotelId}, /rooms/update/{roomId})
 */
@RestController
@RequestMapping("/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService; // Service handles room business logic

    /**
     * POST /rooms/add/{hotelId}?roomType=Deluxe&price=299&capacity=2...
     * Adds a new room to the given hotel. Admin only.
     *
     * WHY @RequestParam instead of @RequestBody?
     * The room data is sent as URL query parameters (not JSON body),
     * because the frontend sends it that way using axios params.
     */
    @PostMapping("/add/{hotelId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> addRoom(
            @PathVariable Long hotelId,
            @RequestParam String roomType,
            @RequestParam Double price,
            @RequestParam Integer capacity,
            @RequestParam(required = false) String description, // Optional field
            @RequestParam(required = false) String amenities    // Optional field
    ) {
        ResponseDTO response = roomService.addNewRoom(hotelId, roomType, price, capacity, description, amenities);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * GET /rooms/available-rooms-by-date-and-type?hotelId=1&checkInDate=2026-05-01&checkOutDate=2026-05-05
     * Returns rooms in a hotel that are available for the given dates.
     * Used on the Hotel Details page when user searches availability.
     */
    @GetMapping("/available-rooms-by-date-and-type")
    public ResponseEntity<ResponseDTO> getAvailableRoomsByDateAndType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date checkOutDate,
            @RequestParam Long hotelId
    ) {
        ResponseDTO response = roomService.getAvailableRoomsByHotelAndDate(hotelId, checkInDate, checkOutDate);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * PUT /rooms/update/{roomId}?roomType=Suite&price=350...
     * Updates an existing room's fields. Admin only.
     * All fields are optional — only the provided ones are updated.
     */
    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> updateRoom(
            @PathVariable Long roomId,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String amenities
    ) {
        ResponseDTO response = roomService.updateRoom(roomId, roomType, price, capacity, description, amenities);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * DELETE /rooms/delete/{roomId}
     * Permanently deletes a room. Admin only.
     */
    @DeleteMapping("/delete/{roomId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> deleteRoom(@PathVariable Long roomId) {
        ResponseDTO response = roomService.deleteRoom(roomId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * GET /rooms/all-rooms-by-hotel/{hotelId}
     * Returns all rooms belonging to a specific hotel.
     */
    @GetMapping("/all-rooms-by-hotel/{hotelId}")
    public ResponseEntity<ResponseDTO> getAllRoomsByHotel(@PathVariable Long hotelId) {
        ResponseDTO response = roomService.getAllRoomsByHotel(hotelId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
