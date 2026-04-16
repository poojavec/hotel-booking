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


@RestController
@RequestMapping("/hotels")
@CrossOrigin(origins = "*")
public class HotelController {

    @Autowired
    private HotelService hotelService; // Service handles database logic

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAllHotels() {
        ResponseDTO response = hotelService.getAllHotels();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getHotelById(@PathVariable Long id) {
        ResponseDTO response = hotelService.getHotelById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchHotels(@RequestParam String location) {
        ResponseDTO response = hotelService.getHotelsByLocation(location);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @GetMapping("/available-hotels-by-date-and-location")
    public ResponseEntity<ResponseDTO> getAvailableHotelsByDateAndLocation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date checkOutDate,
            @RequestParam String location) {
        ResponseDTO response = hotelService.getAvailableHotelsByDateAndLocation(checkInDate, checkOutDate, location);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> addHotel(@RequestBody Hotel hotel) {
        ResponseDTO response = hotelService.addHotel(hotel);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> updateHotel(@PathVariable Long id, @RequestBody Hotel hotel) {
        ResponseDTO response = hotelService.updateHotel(id, hotel);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO> deleteHotel(@PathVariable Long id) {
        ResponseDTO response = hotelService.deleteHotel(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
