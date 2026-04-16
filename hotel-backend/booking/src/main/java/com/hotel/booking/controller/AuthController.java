package com.hotel.booking.controller;

import com.hotel.booking.dto.ResponseDTO;
import com.hotel.booking.entity.User;
import com.hotel.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController handles user registration and login HTTP requests.
 *
 * @RestController = This class handles API requests and returns JSON responses.
 * @RequestMapping("/auth") = All URLs in this class start with /auth
 *                            e.g. /auth/register, /auth/login
 * @CrossOrigin(origins = "*") = Allow requests from ANY frontend URL.
 *                               WHY? Without this, browsers block requests from different ports/domains.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService; // Inject UserService to handle the business logic

    /**
     * POST /auth/register
     * Receives user data from the frontend, calls the service to create the account.
     *
     * @RequestBody = Reads the JSON body from the HTTP request and converts it to a User object.
     * ResponseEntity = Allows us to set both the HTTP status code AND the response body.
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody User user) {
        ResponseDTO response = userService.register(user);
        // response.getStatusCode() = 200 for success, 400 if email already exists, 500 for error
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * POST /auth/login
     * Receives email and password, returns a JWT token if credentials are correct.
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody User loginRequest) {
        ResponseDTO response = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
