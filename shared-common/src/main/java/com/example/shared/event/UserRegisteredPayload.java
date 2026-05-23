package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * Payload for UserRegistered event.
 * Contains user registration information.
 * 
 * Requirements: 4.2
 */
public class UserRegisteredPayload {
    
    @JsonProperty("userId")
    @NotBlank(message = "userId cannot be blank")
    private String userId;
    
    @JsonProperty("username")
    @NotBlank(message = "username cannot be blank")
    private String username;
    
    @JsonProperty("email")
    @NotBlank(message = "email cannot be blank")
    @Email(message = "email must be valid")
    private String email;
    
    // Default constructor for Jackson
    public UserRegisteredPayload() {
    }
    
    public UserRegisteredPayload(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRegisteredPayload that = (UserRegisteredPayload) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(username, that.username) &&
               Objects.equals(email, that.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, username, email);
    }
    
    @Override
    public String toString() {
        return "UserRegisteredPayload{" +
               "userId='" + userId + '\'' +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
