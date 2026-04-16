package com.hotel.booking.security;

import com.hotel.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService tells Spring Security HOW to load a user from the database.
 *
 * WHY do we need this?
 * Spring Security needs to know how to find a user by their username (which is email in our app).
 * By implementing UserDetailsService, we give Spring Security our custom lookup logic.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // To find the user by email from the database

    /**
     * This method is called automatically by Spring Security when verifying a login.
     * It finds the user in the DB and converts them into a Spring Security UserDetails object.
     *
     * @param username = In our app, this is actually the user's EMAIL (not a username)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Look up the user by email
        return userRepository.findByEmail(username)
                .map(user ->
                        // Convert our User entity into a Spring Security UserDetails object
                        org.springframework.security.core.userdetails.User
                                .withUsername(user.getEmail())     // Set email as the "username"
                                .password(user.getPassword())      // Set the hashed password
                                .authorities(user.getRole())       // Set role: "ROLE_USER" or "ROLE_ADMIN"
                                .build()
                )
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}
