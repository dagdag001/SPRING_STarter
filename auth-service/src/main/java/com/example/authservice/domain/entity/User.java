package com.example.authservice.domain.entity;

import com.example.authservice.domain.valueobject.Email;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a user in the system.
 * Contains user identification and authentication information.
 * 
 * Requirements: 1.1, 4.1
 */
public class User {
    
    private final String userId;
    private final String username;
    private final Email email;
    private final String passwordHash;
    private final Instant createdAt;
    
    /**
     * Constructor for creating a new user.
     * 
     * @param username The unique username
     * @param email The user's email address
     * @param passwordHash The hashed password
     */
    public User(String username, Email email, String passwordHash) {
        this(UUID.randomUUID().toString(), username, email, passwordHash, Instant.now());
    }
    
    /**
     * Constructor for reconstituting a user from persistence.
     * 
     * @param userId The unique identifier
     * @param username The unique username
     * @param email The user's email address
     * @param passwordHash The hashed password
     * @param createdAt The timestamp when the user was created
     */
    public User(String userId, String username, Email email, String passwordHash, Instant createdAt) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId cannot be null or empty");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username cannot be null or empty");
        }
        if (email == null) {
            throw new IllegalArgumentException("email cannot be null");
        }
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("passwordHash cannot be null or empty");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt cannot be null");
        }
        
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public Email getEmail() {
        return email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return "User{" +
               "userId='" + userId + '\'' +
               ", username='" + username + '\'' +
               ", email=" + email +
               ", createdAt=" + createdAt +
               '}';
    }
}
