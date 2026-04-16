package com.hotel.booking.service;

import com.hotel.booking.dto.ResponseDTO;
import com.hotel.booking.dto.UserDTO;
import com.hotel.booking.entity.User;
import com.hotel.booking.repository.UserRepository;
import com.hotel.booking.security.JWTUtils;
import com.hotel.booking.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * UserService handles the business logic for user registration and login.
 * @Service tells Spring this is a service layer class — Spring will manage it.
 * @Autowired tells Spring to automatically inject the needed objects.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository; // Used to read/write users from the database

    @Autowired
    private PasswordEncoder passwordEncoder; // Used to hash (encrypt) the password before saving

    @Autowired
    private JWTUtils jwtUtils; // Used to generate a JWT token after login

    @Autowired
    private AuthenticationManager authenticationManager; // Spring Security's login checker

    @Autowired
    private EmailService emailService; // Used to send emails to the user

    /**
     * REGISTER: Creates a new user account.
     * Steps: Check email not taken → hash password → save to DB → send welcome email
     */
    public ResponseDTO register(User user) {
        ResponseDTO response = new ResponseDTO();
        try {
            // Step 1: Check if this email is already registered. If yes, reject.
            if (userRepository.existsByEmail(user.getEmail())) {
                response.setStatusCode(400);
                response.setMessage("Email already exists");
                return response; // Stop here and return the error
            }

            // Step 2: Hash the password using BCrypt before saving.
            // WHY? We never store plain text passwords — only the encrypted version.
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Step 3: Save the user to the database
            User savedUser = userRepository.save(user);

            // Step 4: Convert to DTO (safe version without password)
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);

            // Step 5: Send a welcome email to the user
            emailService.sendRegistrationEmail(savedUser.getEmail(), savedUser.getFirstName());

            // Step 6: Build and return a success response
            response.setStatusCode(200);
            response.setMessage("User registered successfully");
            response.setUser(userDTO);

        } catch (Exception e) {
            // If anything goes wrong, return a 500 error
            response.setStatusCode(500);
            response.setMessage("Error registering user: " + e.getMessage());
        }
        return response;
    }

    /**
     * LOGIN: Checks credentials and returns a JWT token if correct.
     * Steps: Authenticate → get user → generate JWT token → return response
     */
    public ResponseDTO login(String email, String password) {
        ResponseDTO response = new ResponseDTO();
        try {
            // Step 1: Ask Spring Security to verify the email + password.
            // If the credentials are wrong, it throws an exception automatically.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // Step 3: Load the user from the DB (we know they exist now)
            User user = userRepository.findByEmail(email).orElseThrow();

            // Step 4: Build a Spring Security UserDetails object (needed to generate JWT)
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    org.springframework.security.core.userdetails.User
                            .withUsername(user.getEmail())
                            .password(user.getPassword())
                            .authorities(user.getRole())
                            .build();

            // Step 5: Generate a JWT token for this user
            // WHY JWT? After login, the frontend keeps this token and sends it with every request
            // to prove who they are — so they don't have to log in again on every click.
            String token = jwtUtils.generateToken(userDetails);

            // Step 6: Return success response with token, role, and user info
            response.setStatusCode(200);
            response.setMessage("Login successful");
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("24H");
            response.setUser(Utils.mapUserEntityToUserDTO(user));

        } catch (Exception e) {
            // If login fails (wrong password), return 401 Unauthorized
            response.setStatusCode(401);
            response.setMessage("Invalid email or password");
        }
        return response;
    }

    /**
     * GET PROFILE: Returns profile info for a given email.
     */
    public ResponseDTO getUserProfile(String email) {
        ResponseDTO response = new ResponseDTO();
        try {
            User user = userRepository.findByEmail(email).orElseThrow();
            response.setStatusCode(200);
            response.setUser(Utils.mapUserEntityToUserDTO(user));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting user profile: " + e.getMessage());
        }
        return response;
    }
}
