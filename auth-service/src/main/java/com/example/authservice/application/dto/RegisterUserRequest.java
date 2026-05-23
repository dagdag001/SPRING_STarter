package com.example.authservice.application.dto;

import java.util.Objects;

/**
 * DTO for user registration requests.
 * 
 * Requirements: 1.2, 4.1
 */
public class RegisterUserRequest {
    
    private String username;
    private String email;
    private String password;
    
    public RegisterUserRequest() {
    }
    
    public RegisterUserRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterUserRequest that = (RegisterUserRequest) o;
        return Objects.equals(username, that.username) &&
               Objects.equals(email, that.email) &&
               Objects.equals(password, that.password);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username, email, password);
    }
    
    @Override
    public String toString() {
        return "RegisterUserRequest{" +
               "username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", password='[REDACTED]'" +
               '}';
    }
}
