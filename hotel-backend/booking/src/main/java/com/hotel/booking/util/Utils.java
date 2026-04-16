package com.hotel.booking.util;

import com.hotel.booking.dto.BookingDTO;
import com.hotel.booking.dto.HotelDTO;
import com.hotel.booking.dto.RoomDTO;
import com.hotel.booking.dto.UserDTO;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Hotel;
import com.hotel.booking.entity.Room;
import com.hotel.booking.entity.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils = Utility class.
 * This class holds helper methods that convert database entities into DTOs.
 * WHY do we need this? Because we don't want to send the raw database entities
 * to the frontend — they can cause loops or expose sensitive data (like passwords).
 * So we copy only the safe fields into a DTO (Data Transfer Object) and send that.
 */
public class Utils {

    // Converts a User (from database) → UserDTO (safe to send to frontend)
    public static UserDTO mapUserEntityToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        // Note: We do NOT copy the password — that must stay private!
        return dto;
    }

    // Converts a Hotel (from database) → HotelDTO (without rooms list)
    public static HotelDTO mapHotelEntityToHotelDTO(Hotel hotel) {
        HotelDTO dto = new HotelDTO();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setLocation(hotel.getLocation());
        dto.setDescription(hotel.getDescription());
        dto.setRating(hotel.getRating());
        dto.setThumbnailUrl(hotel.getThumbnailUrl());
        dto.setAmenities(hotel.getAmenities());
        return dto;
    }

    // Converts a Room (from database) → RoomDTO (without hotel info)
    public static RoomDTO mapRoomEntityToRoomDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setRoomType(room.getRoomType());
        dto.setPricePerNight(room.getPricePerNight());
        dto.setCapacity(room.getCapacity());
        dto.setDescription(room.getDescription());
        dto.setAmenities(room.getAmenities());
        dto.setIsAvailable(room.getIsAvailable());
        return dto;
    }

    // Converts a Booking → BookingDTO (without user or room info)
    public static BookingDTO mapBookingEntityToBookingDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setBookingStatus(booking.getBookingStatus());
        dto.setBookingConfirmationCode(booking.getBookingConfirmationCode());
        return dto;
    }

    // Converts a Hotel → HotelDTO AND also includes the list of rooms inside it.
    // WHY two methods? Sometimes we just need hotel info (faster), sometimes we need rooms too.
    public static HotelDTO mapHotelEntityToHotelDTOPlusRooms(Hotel hotel) {
        HotelDTO hotelDTO = mapHotelEntityToHotelDTO(hotel); // Start with basic hotel info

        // If the hotel has rooms, convert each room and add to the DTO
        if (hotel.getRooms() != null) {
            List<RoomDTO> roomDTOs = new ArrayList<>();
            for (Room room : hotel.getRooms()) {
                roomDTOs.add(mapRoomEntityToRoomDTO(room));
            }
            hotelDTO.setRooms(roomDTOs);
        }
        return hotelDTO;
    }

    // Converts a Booking → BookingDTO AND also includes user + room + hotel info inside.
    // This is the "full" version used when showing booking history on the profile page.
    public static BookingDTO mapBookingEntityToBookingDTOPlusAll(Booking booking) {
        BookingDTO bookingDTO = mapBookingEntityToBookingDTO(booking); // Start with basic booking info

        // Include user info (who made the booking)
        if (booking.getUser() != null) {
            bookingDTO.setUser(mapUserEntityToUserDTO(booking.getUser()));
        }

        // Include room info (which room was booked)
        if (booking.getRoom() != null) {
            RoomDTO roomDTO = mapRoomEntityToRoomDTO(booking.getRoom());
            // Also include which hotel this room belongs to
            if (booking.getRoom().getHotel() != null) {
                roomDTO.setHotel(mapHotelEntityToHotelDTO(booking.getRoom().getHotel()));
            }
            bookingDTO.setRoom(roomDTO);
        }
        return bookingDTO;
    }
}
