package com.example.authservice.application.dto;

import java.util.Objects;

/**
 * DTO for authentication requests.
 * 
 * Requirements: 1.2, 4.3
 */
public class AuthenticationRequest {
    
    private String username;
    private String password;
    
    public AuthenticationRequest() {
    }
    
    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
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
        AuthenticationRequest that = (AuthenticationRequest) o;
        return Objects.equals(username, that.username) &&
               Objects.equals(password, that.password);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
    
    @Override
    public String toString() {
        return "AuthenticationRequest{" +
               "username='" + username + '\'' +
               ", password='[REDACTED]'" +
               '}';
    }
}
