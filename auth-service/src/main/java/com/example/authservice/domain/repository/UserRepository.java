package com.example.authservice.domain.repository;

import com.example.authservice.domain.entity.User;
import com.example.authservice.domain.valueobject.Email;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Defines contracts for user persistence operations.
 * 
 * Requirements: 1.1, 4.1
 */
public interface UserRepository {
    
    /**
     * Saves a user to the repository.
     * 
     * @param user The user to save
     * @return The saved user
     */
    User save(User user);
    
    /**
     * Finds a user by their unique identifier.
     * 
     * @param userId The user ID
     * @return An Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(String userId);
    
    /**
     * Finds a user by their username.
     * 
     * @param username The username
     * @return An Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Finds a user by their email address.
     * 
     * @param email The email address
     * @return An Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(Email email);
    
    /**
     * Checks if a username already exists.
     * 
     * @param username The username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Checks if an email already exists.
     * 
     * @param email The email to check
     * @return true if the email exists, false otherwise
     */
    boolean existsByEmail(Email email);
}
