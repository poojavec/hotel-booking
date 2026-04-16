package com.hotel.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// This class represents a Hotel in our system.
// Lombok @Data gives us getters/setters automatically.
@Data
@Entity
@Table(name = "hotels") // Maps to the "hotels" table in the database
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    // Auto-generated primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Hotel name, e.g. "Grand Royal Palace"

    @Column(nullable = false)
    private String location; // e.g. "Paris, France"

    // columnDefinition = "TEXT" allows storing long text (more than 255 chars)
    @Column(columnDefinition = "TEXT")
    private String description; // Full description of the hotel

    private Double rating = 0.0; // Star rating, default is 0.0

    private String thumbnailUrl; // URL of the hotel's cover image

    private String amenities; // Comma-separated list like "Pool, Spa, Gym"

    // One hotel can have many rooms.
    // mappedBy = "hotel" means the "hotel" field in the Room class owns this relationship.
    // CascadeType.ALL means if a hotel is deleted, all its rooms are also deleted.
    // FetchType.LAZY means rooms are loaded from DB only when we actually ask for them (efficient).
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Room> rooms;
}
