package com.hotel.booking.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * SecurityConfig is the main Spring Security configuration class.
 * It controls WHO can access WHICH endpoints, and HOW authentication works.
 *
 * @Configuration = This class provides Spring beans (objects Spring manages)
 * @EnableWebSecurity = Enables Spring Security for this app
 * @EnableMethodSecurity = Enables @PreAuthorize annotations in controllers
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // Loads users from our DB

    @Autowired
    private JWTAuthFilter jwtAuthFilter; // Our custom filter that validates JWT tokens

    /**
     * Defines the security rules for all HTTP requests.
     * Think of this as the "gateway" that decides who can go where.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Disable CSRF (Cross-Site Request Forgery) protection.
                // WHY? CSRF is for session-based apps. We use JWT (stateless), so CSRF isn't needed.
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS (Cross-Origin Resource Sharing) with our custom settings below.
                // WHY? The frontend runs on localhost:5173 and backend on localhost:8080 — different origins.
                .cors(Customizer.withDefaults())

                // Define access rules for each URL pattern:
                .authorizeHttpRequests(request -> request
                        // These endpoints are PUBLIC — no login required
                        .requestMatchers("/auth/**", "/hotels/**", "/rooms/**").permitAll()
                        // Permit Swagger UI and OpenAPI documentation
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Everything else (e.g. /bookings/**) requires authentication
                        .anyRequest().authenticated()
                )

                // Use STATELESS sessions.
                // WHY? With JWT, the server doesn't store any session. Each request carries the token.
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Set our custom login verification logic
                .authenticationProvider(authenticationProvider())

                // Add our JWT filter BEFORE Spring Security's default login filter.
                // WHY? We want to validate JWT tokens before Spring tries its own login check.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    /**
     * Configures HOW Spring Security verifies username + password at login.
     * DaoAuthenticationProvider = uses our database (via UserDetailsService) + password encoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService); // Use our DB user lookup
        provider.setPasswordEncoder(passwordEncoder());           // Use BCrypt to verify password
        return provider;
    }

    /**
     * BCryptPasswordEncoder hashes passwords using the BCrypt algorithm.
     * WHY BCrypt? It's slow by design — makes brute-force hacking very hard.
     * The same password hashed twice gives different results, making it extra secure.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * The AuthenticationManager is used in UserService to verify login credentials.
     * Spring provides this automatically from the configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS configuration — defines which frontend origins, HTTP methods, and headers are allowed.
     * WHY? Browsers block requests between different origins by default. This allows our frontend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from our React frontend (both possible ports)
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174"));

        // Allow these HTTP methods from the frontend
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow these headers in requests (Authorization = JWT token, Content-Type = JSON)
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // Allow cookies to be sent with requests
        config.setAllowCredentials(true);

        // Apply this CORS config to ALL URL patterns
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
