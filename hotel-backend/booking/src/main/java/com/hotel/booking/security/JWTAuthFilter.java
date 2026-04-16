package com.hotel.booking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * JWTAuthFilter is a Spring Security filter that runs ONCE for every HTTP request.
 *
 * WHAT does it do?
 * It checks if the incoming request has a valid JWT token in the Authorization header.
 * If the token is valid, it tells Spring Security that this user is authenticated.
 * If there's no token or it's invalid, the request continues without authentication
 * (and protected endpoints will return 401 Unauthorized).
 *
 * HOW does authentication work in our app (flow):
 * 1. User logs in → gets a JWT token
 * 2. Frontend stores the token in localStorage
 * 3. For every authenticated request, frontend sends: Authorization: Bearer <token>
 * 4. This filter reads that header, validates the token, and sets the user as "logged in"
 */
@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils; // For validating and reading the JWT token

    @Autowired
    private CustomUserDetailsService userDetailsService; // For loading user from database

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Get the Authorization header from the request
        // Example value: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        String authHeader = request.getHeader("Authorization");

        // Step 2: If there's no Authorization header, or it doesn't start with "Bearer ",
        // skip this filter and continue the request chain without setting authentication
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract the actual token by removing the "Bearer " prefix (7 characters)
        String jwt = authHeader.substring(7);

        // Step 4: Extract the email (username) from inside the token
        String userEmail = jwtUtils.extractUsername(jwt);

        // Step 5: If we got an email AND no one is already authenticated in this request
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 6: Load the full user details from the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // Step 7: Validate the token (correct user? not expired?)
            if (jwtUtils.isValidToken(jwt, userDetails)) {

                // Step 8: Create a Spring Security authentication object with the user's details and roles
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,             // Who the user is
                        null,                    // Credentials (not needed here — token handles this)
                        userDetails.getAuthorities() // Their roles (e.g. ROLE_USER or ROLE_ADMIN)
                );

                // Step 9: Attach request details (like IP address) to the auth token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Step 10: Tell Spring Security: "This user is authenticated!"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 11: Continue to the next filter or the actual controller
        filterChain.doFilter(request, response);
    }
}
