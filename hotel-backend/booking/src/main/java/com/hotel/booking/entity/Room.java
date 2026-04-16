package com.hotel.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

// This class represents a Room inside a Hotel.
@Data
@Entity
@Table(name = "rooms") // Maps to "rooms" table in the database
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    // Auto-generated primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many rooms belong to one hotel (Many-to-One relationship)
    // FetchType.LAZY = only load the hotel from DB when we specifically need it
    // @JoinColumn defines the foreign key column name in the "rooms" table
    // @JsonIgnore prevents infinite loops when converting to JSON (Hotel -> Rooms -> Hotel -> ...)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonIgnore
    private Hotel hotel;

    @Column(nullable = false)
    private String roomType; // e.g. "Deluxe Suite", "Standard Room"

    @Column(nullable = false)
    private Double pricePerNight; // Price for one night in USD

    @Column(nullable = false)
    private Integer capacity; // Max number of guests allowed

    @Column(columnDefinition = "TEXT")
    private String description; // Description of the room features

    private String amenities; // Comma-separated list like "TV, AC, Mini Bar"

    private Boolean isAvailable = true; // true = room can be booked
}
