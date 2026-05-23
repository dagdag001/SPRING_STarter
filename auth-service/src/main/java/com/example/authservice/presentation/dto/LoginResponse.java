package com.example.authservice.presentation.dto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * REST API response DTO for user login.
 * 
 * Requirements: 1.4, 14.2
 */
public class LoginResponse {
    
    private String token;
    private String expiresAt;
    
    public LoginResponse() {
    }
    
    public LoginResponse(String token, long expirationHours) {
        this.token = token;
        this.expiresAt = Instant.now().plus(expirationHours, ChronoUnit.HOURS).toString();
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginResponse that = (LoginResponse) o;
        return Objects.equals(token, that.token) &&
               Objects.equals(expiresAt, that.expiresAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(token, expiresAt);
    }
    
    @Override
    public String toString() {
        return "LoginResponse{" +
               "token='[REDACTED]'" +
               ", expiresAt='" + expiresAt + '\'' +
               '}';
    }
}
