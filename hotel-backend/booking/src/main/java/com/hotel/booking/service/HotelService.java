package com.hotel.booking.service;

import com.hotel.booking.dto.HotelDTO;
import com.hotel.booking.dto.ResponseDTO;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.repository.HotelRepository;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.exception.OurException;
import com.hotel.booking.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * HotelService handles all business logic for hotels.
 * The Controller receives HTTP requests and calls these service methods.
 * The service does the actual work (database queries, business rules).
 */
@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository; // Read/write hotels from the database

    @Autowired
    private RoomRepository roomRepository; // Used to check room availability

    /**
     * GET ALL HOTELS: Returns the full list of all hotels.
     */
    public ResponseDTO getAllHotels() {
        ResponseDTO response = new ResponseDTO();
        try {
            List<Hotel> hotels = hotelRepository.findAll(); // Get all hotels from DB

            // Convert each Hotel entity to a HotelDTO (with its rooms included)
            List<HotelDTO> hotelDTOs = new ArrayList<>();
            for (Hotel hotel : hotels) {
                hotelDTOs.add(Utils.mapHotelEntityToHotelDTOPlusRooms(hotel));
            }

            response.setStatusCode(200);
            response.setMessage("Hotels retrieved successfully");
            response.setHotelList(hotelDTOs);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving hotels: " + e.getMessage());
        }
        return response;
    }

    /**
     * GET HOTEL BY ID: Returns one specific hotel by its database ID.
     */
    public ResponseDTO getHotelById(Long hotelId) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (hotelId == null) throw new OurException("Hotel ID is required");
            // Find hotel in DB — orElseThrow() throws error if not found
            Hotel hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new OurException("Hotel not found"));
            response.setStatusCode(200);
            response.setHotel(Utils.mapHotelEntityToHotelDTOPlusRooms(hotel));
        } catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage("Hotel not found with id: " + hotelId);
        }
        return response;
    }

    /**
     * SEARCH BY LOCATION: Returns hotels in a specific city/location.
     * The search is case-insensitive and partial (e.g. "par" finds "Paris, France").
     */
    public ResponseDTO getHotelsByLocation(String location) {
        ResponseDTO response = new ResponseDTO();
        try {
            List<Hotel> hotels = hotelRepository.findByLocationContainingIgnoreCase(location);

            // Convert each hotel to DTO
            List<HotelDTO> hotelDTOs = new ArrayList<>();
            for (Hotel hotel : hotels) {
                hotelDTOs.add(Utils.mapHotelEntityToHotelDTOPlusRooms(hotel));
            }

            response.setStatusCode(200);
            response.setHotelList(hotelDTOs);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error searching hotels: " + e.getMessage());
        }
        return response;
    }

    /**
     * AVAILABLE HOTELS BY DATE AND LOCATION:
     * Finds hotels in a location that have at least one available room for the given dates.
     * Used on the homepage search feature.
     */
    public ResponseDTO getAvailableHotelsByDateAndLocation(Date checkInDate, Date checkOutDate, String location) {
        ResponseDTO response = new ResponseDTO();
        try {
            // Step 1: Get all hotels in the location
            List<Hotel> hotels = hotelRepository.findByLocationContainingIgnoreCase(location);

            // Step 2: Keep only hotels that have at least one available room for these dates
            List<HotelDTO> availableHotels = new ArrayList<>();
            for (Hotel hotel : hotels) {
                // Check if this hotel has any available rooms for the date range
                boolean hasAvailableRooms = !roomRepository
                        .findAvailableRoomsByHotelAndDate(hotel.getId(), checkInDate, checkOutDate)
                        .isEmpty();

                if (hasAvailableRooms) {
                    availableHotels.add(Utils.mapHotelEntityToHotelDTOPlusRooms(hotel));
                }
            }

            response.setStatusCode(200);
            response.setMessage("Available hotels retrieved successfully");
            response.setHotelList(availableHotels);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error searching available hotels: " + e.getMessage());
        }
        return response;
    }

    /**
     * ADD HOTEL: Saves a new hotel to the database (Admin only).
     */
    public ResponseDTO addHotel(Hotel hotel) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (hotel == null) throw new OurException("Hotel details are required");
            Hotel savedHotel = hotelRepository.save(hotel); // Save to database
            response.setStatusCode(200);
            response.setMessage("Hotel added successfully");
            response.setHotel(Utils.mapHotelEntityToHotelDTOPlusRooms(savedHotel));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error adding hotel: " + e.getMessage());
        }
        return response;
    }

    /**
     * UPDATE HOTEL: Updates fields of an existing hotel (Admin only).
     * Only updates fields that are provided (null fields are skipped).
     */
    public ResponseDTO updateHotel(Long hotelId, Hotel hotelDetails) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (hotelId == null) throw new OurException("Hotel ID is required for update");
            // Find the existing hotel
            Hotel hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new OurException("Hotel not found"));

            // Only update fields that were actually sent — skip nulls
            if (hotelDetails.getName() != null) hotel.setName(hotelDetails.getName());
            if (hotelDetails.getLocation() != null) hotel.setLocation(hotelDetails.getLocation());
            if (hotelDetails.getDescription() != null) hotel.setDescription(hotelDetails.getDescription());
            if (hotelDetails.getThumbnailUrl() != null) hotel.setThumbnailUrl(hotelDetails.getThumbnailUrl());
            if (hotelDetails.getAmenities() != null) hotel.setAmenities(hotelDetails.getAmenities());
            if (hotelDetails.getRating() != 0) hotel.setRating(hotelDetails.getRating());

            if (hotel == null) throw new OurException("Hotel object is null before save");
            Hotel updatedHotel = hotelRepository.save(hotel); // Save changes
            response.setStatusCode(200);
            response.setMessage("Hotel updated successfully");
            response.setHotel(Utils.mapHotelEntityToHotelDTOPlusRooms(updatedHotel));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating hotel: " + e.getMessage());
        }
        return response;
    }

    /**
     * DELETE HOTEL: Permanently removes a hotel from the database (Admin only).
     * Because of CascadeType.ALL on Hotel, all its rooms are also deleted automatically.
     */
    public ResponseDTO deleteHotel(Long hotelId) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (hotelId == null) throw new OurException("Hotel ID is required for deletion");
            hotelRepository.deleteById(hotelId); // Delete from database
            response.setStatusCode(200);
            response.setMessage("Hotel deleted successfully");
        } catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage("Hotel not found or error deleting: " + e.getMessage());
        }
        return response;
    }
}
