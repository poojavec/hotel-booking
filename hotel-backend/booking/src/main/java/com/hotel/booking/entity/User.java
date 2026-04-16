package com.hotel.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

// @Data from Lombok automatically creates getters, setters, toString, equals, and hashCode methods.
// This saves us from writing a lot of repetitive code.
@Data
// @Entity tells Spring/JPA that this class maps to a database table.
@Entity
// @Table(name = "users") sets the actual table name in the database.
@Table(name = "users")
// @NoArgsConstructor creates an empty constructor: new User()
@NoArgsConstructor
// @AllArgsConstructor creates a constructor with all fields as parameters
@AllArgsConstructor
public class User {

    // @Id marks this as the primary key of the table
    // @GeneratedValue means the DB will auto-increment and assign the ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(nullable = false) means this field CANNOT be empty in the database
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // unique = true means no two users can have the same email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // This will be stored as a hashed (encrypted) password

    // Default role is ROLE_USER. Admin role is ROLE_ADMIN.
    private String role = "ROLE_USER";

    // @Temporal tells JPA how to store the Date type — TIMESTAMP includes date + time
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(); // Automatically set to the current date/time when created
}
