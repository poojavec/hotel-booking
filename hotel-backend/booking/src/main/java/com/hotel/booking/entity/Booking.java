package com.hotel.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

// This class represents a hotel room booking made by a user.
@Data
@Entity
@Table(name = "bookings") // Maps to "bookings" table in the database
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    // Auto-generated primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each booking belongs to one user.
    // FetchType.EAGER = automatically load the User data whenever a Booking is fetched.
    // @JoinColumn = the foreign key column in the "bookings" table
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Each booking is for one room.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private Date checkInDate; // Date the guest checks in

    @Column(nullable = false)
    private Date checkOutDate; // Date the guest checks out

    @Column(nullable = false)
    private Double totalAmount; // Total price = (number of nights) x (price per night)

    // Status can be "CONFIRMED" or "CANCELLED"
    @Column(nullable = false)
    private String bookingStatus = "CONFIRMED"; // Default is CONFIRMED when a booking is created

    // A short unique code given to the user as booking proof (e.g. "A3F8B2C1")
    @Column(unique = true, nullable = false)
    private String bookingConfirmationCode;

    // Automatically records when the booking was created
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}
