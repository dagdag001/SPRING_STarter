package com.example.authservice.presentation.dto;

import java.time.Instant;
import java.util.Objects;

/**
 * REST API response DTO for user registration.
 * 
 * Requirements: 1.4, 14.1
 */
public class RegisterResponse {
    
    private String userId;
    private String username;
    private String email;
    private String createdAt;
    
    public RegisterResponse() {
    }
    
    public RegisterResponse(String userId, String username, String email, Instant createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt.toString();
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterResponse that = (RegisterResponse) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(username, that.username) &&
               Objects.equals(email, that.email) &&
               Objects.equals(createdAt, that.createdAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, username, email, createdAt);
    }
    
    @Override
    public String toString() {
        return "RegisterResponse{" +
               "userId='" + userId + '\'' +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", createdAt='" + createdAt + '\'' +
               '}';
    }
}
