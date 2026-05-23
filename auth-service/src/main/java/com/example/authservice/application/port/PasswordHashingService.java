package com.example.authservice.application.port;

/**
 * Port interface for password hashing operations.
 * Implemented in the infrastructure layer.
 * 
 * Requirements: 1.2, 4.1, 4.3
 */
public interface PasswordHashingService {
    
    /**
     * Hashes a plain text password.
     * 
     * @param plainPassword The plain text password
     * @return The hashed password
     */
    String hashPassword(String plainPassword);
    
    /**
     * Verifies a plain text password against a hashed password.
     * 
     * @param plainPassword The plain text password
     * @param hashedPassword The hashed password
     * @return true if the password matches, false otherwise
     */
    boolean verifyPassword(String plainPassword, String hashedPassword);
}
