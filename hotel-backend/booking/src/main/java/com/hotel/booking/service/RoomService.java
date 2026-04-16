package com.hotel.booking.service;

import com.hotel.booking.dto.ResponseDTO;
import com.hotel.booking.dto.RoomDTO;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.entity.Room;
import com.hotel.booking.repository.HotelRepository;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.exception.OurException;
import com.hotel.booking.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * RoomService handles all business logic for hotel rooms.
 * @Transactional = if anything fails in a method, all DB changes are rolled back.
 */
@Service
@Transactional
public class RoomService {

    @Autowired
    private RoomRepository roomRepository; // For room database operations

    @Autowired
    private HotelRepository hotelRepository; // To find the hotel a room belongs to

    /**
     * ADD ROOM: Creates a new room and links it to a hotel (Admin only).
     */
    public ResponseDTO addNewRoom(Long hotelId, String roomType, Double price, Integer capacity, String description, String amenities) {
        ResponseDTO response = new ResponseDTO();
        try {
            // Step 1: Basic validation - ensure hotelId is not null
            if (hotelId == null) throw new OurException("Hotel ID is required to add a room");

            // Find the hotel this room will belong to
            Hotel hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new OurException("Hotel not found"));

            // Create a new Room and fill in all the details
            Room room = new Room();
            room.setHotel(hotel);           // Link this room to the hotel
            room.setRoomType(roomType);     // e.g. "Deluxe Suite"
            room.setPricePerNight(price);   // e.g. 299.99
            room.setCapacity(capacity);     // e.g. 2 guests
            room.setDescription(description);
            room.setAmenities(amenities);   // e.g. "TV, AC, Mini Bar"

            roomRepository.save(room); // Save to database

            response.setStatusCode(200);
            response.setMessage("Room added successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error adding room: " + e.getMessage());
        }
        return response;
    }

    /**
     * GET AVAILABLE ROOMS BY DATE: Returns all rooms for a hotel that are
     * free (not already booked) for the given check-in and check-out dates.
     */
    public ResponseDTO getAvailableRoomsByHotelAndDate(Long hotelId, Date checkIn, Date checkOut) {
        ResponseDTO response = new ResponseDTO();
        try {
            // The repository handles the complex availability query for us
            List<Room> rooms = roomRepository.findAvailableRoomsByHotelAndDate(hotelId, checkIn, checkOut);

            // Convert each Room to a RoomDTO
            List<RoomDTO> roomDTOs = new ArrayList<>();
            for (Room room : rooms) {
                roomDTOs.add(Utils.mapRoomEntityToRoomDTO(room));
            }

            response.setStatusCode(200);
            response.setRoomList(roomDTOs);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving available rooms: " + e.getMessage());
        }
        return response;
    }

    /**
     * UPDATE ROOM: Updates room details (Admin only).
     * Only updates fields that were actually sent (null fields are skipped).
     */
    public ResponseDTO updateRoom(Long roomId, String roomType, Double price, Integer capacity, String description, String amenities) {
        ResponseDTO response = new ResponseDTO();
        try {
            // Step 1: Basic validation - ensure roomId is not null
            if (roomId == null) throw new OurException("Room ID is required to update a room");

            // Find the room to update
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new OurException("Room not found"));

            // Only update if the new value was actually provided
            if (roomType != null) room.setRoomType(roomType);
            if (price != null) room.setPricePerNight(price);
            if (capacity != null) room.setCapacity(capacity);
            if (description != null) room.setDescription(description);
            if (amenities != null) room.setAmenities(amenities);
            roomRepository.save(room); // Save updated room

            response.setStatusCode(200);
            response.setMessage("Room updated successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating room: " + e.getMessage());
        }
        return response;
    }

    /**
     * DELETE ROOM: Permanently removes a room from the database (Admin only).
     */
    public ResponseDTO deleteRoom(Long roomId) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (roomId == null) throw new OurException("Room ID is required to delete a room");
            roomRepository.deleteById(roomId); // Delete from database
            response.setStatusCode(200);
            response.setMessage("Room deleted successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting room: " + e.getMessage());
        }
        return response;
    }

    /**
     * GET ALL ROOMS BY HOTEL: Returns every room belonging to a specific hotel.
     */
    public ResponseDTO getAllRoomsByHotel(Long hotelId) {
        ResponseDTO response = new ResponseDTO();
        try {
            List<Room> rooms = roomRepository.findByHotelId(hotelId);

            // Convert each Room to a RoomDTO
            List<RoomDTO> roomDTOs = new ArrayList<>();
            for (Room room : rooms) {
                roomDTOs.add(Utils.mapRoomEntityToRoomDTO(room));
            }

            response.setStatusCode(200);
            response.setRoomList(roomDTOs);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving rooms: " + e.getMessage());
        }
        return response;
    }
}
