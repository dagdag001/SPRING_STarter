package com.example.authservice.application.port;

/**
 * Port interface for JWT token operations.
 * Implemented in the infrastructure layer.
 * 
 * Requirements: 1.2, 4.3, 4.5, 4.6
 */
public interface JwtTokenService {
    
    /**
     * Generates a JWT token for a user.
     * 
     * @param userId The user's unique identifier
     * @param username The user's username
     * @return The generated JWT token
     */
    String generateToken(String userId, String username);
    
    /**
     * Validates a JWT token.
     * 
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);
    
    /**
     * Extracts the user ID from a JWT token.
     * 
     * @param token The JWT token
     * @return The user ID
     */
    String extractUserId(String token);
    
    /**
     * Extracts the username from a JWT token.
     * 
     * @param token The JWT token
     * @return The username
     */
    String extractUsername(String token);
}
