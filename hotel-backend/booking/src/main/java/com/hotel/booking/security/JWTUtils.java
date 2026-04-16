package com.hotel.booking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * JWTUtils handles everything related to JWT (JSON Web Token).
 *
 * WHAT is a JWT?
 * JWT is a compact, secure token we give to users after they log in.
 * The frontend stores this token and sends it with every request (in the "Authorization" header).
 * The backend checks this token to know WHO is making the request and WHAT role they have.
 *
 * WHY JWT instead of sessions?
 * Our backend is stateless — it doesn't remember who's logged in.
 * JWT tokens contain all the info the server needs, encoded and signed securely.
 */
@Service
public class JWTUtils {

    // These values come from application.properties file
    @Value("${hotel.booking.jwtSecret}")
    private String jwtSecret; // The secret key used to sign tokens (keep this private!)

    @Value("${hotel.booking.jwtExpirationMs}")
    private long jwtExpirationMs; // How long a token is valid in milliseconds (e.g. 86400000 = 24 hours)

    /**
     * Creates a cryptographic signing key from the secret string.
     * WHY? JWT tokens must be signed/verified using a key — this prevents tampering.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes); // Creates an HMAC-SHA key from the bytes
    }

    /**
     * Generates a new JWT token for a logged-in user.
     * The token contains: email, issue time, expiry time — all signed with our secret key.
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())                         // Store email inside token
                .setIssuedAt(new Date(System.currentTimeMillis()))             // When the token was created
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // When it expires
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)           // Sign with our secret key
                .compact(); // Build and return the token string
    }

    /**
     * Reads the email (subject) from inside the token.
     * Used to identify WHO sent the request.
     */
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Generic helper to extract any "claim" from a JWT token.
     * Claims = the data stored inside the token (email, expiry, etc.)
     */
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        // Parse the token, verify its signature, then extract the requested claim
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsTFunction.apply(claims);
    }

    /**
     * Checks if a token is valid:
     * 1. The email in the token must match the logged-in user's email
     * 2. The token must not be expired
     */
    public boolean isValidToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Checks if the token's expiry time has passed.
     * Returns true if the token is expired (no longer valid).
     */
    private boolean isTokenExpired(String token) {
        Date expirationDate = extractClaims(token, Claims::getExpiration);
        return expirationDate.before(new Date()); // If expiry is before now, it's expired
    }
}
