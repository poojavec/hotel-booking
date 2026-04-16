package com.hotel.booking.repository;

import com.hotel.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * UserRepository handles all database operations for User.
 * Extending JpaRepository gives us free CRUD methods (save, findById, findAll, delete...).
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by their email address.
    // Returns Optional<User> because the user might NOT exist — we handle that safely.
    // Spring JPA auto-generates: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Check if a user with this email already exists in the database.
    // Returns true or false. Used during registration to prevent duplicate accounts.
    // Spring JPA auto-generates: SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);
}
