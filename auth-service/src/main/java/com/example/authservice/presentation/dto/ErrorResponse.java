package com.example.authservice.presentation.dto;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * REST API error response DTO.
 * 
 * Requirements: 1.4, 14.6
 */
public class ErrorResponse {
    
    private String error;
    private String message;
    private String timestamp;
    private String path;
    private Map<String, String> details;
    
    public ErrorResponse() {
    }
    
    public ErrorResponse(String error, String message, String path) {
        this(error, message, path, null);
    }
    
    public ErrorResponse(String error, String message, String path, Map<String, String> details) {
        this.error = error;
        this.message = message;
        this.timestamp = Instant.now().toString();
        this.path = path;
        this.details = details;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public Map<String, String> getDetails() {
        return details;
    }
    
    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return Objects.equals(error, that.error) &&
               Objects.equals(message, that.message) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(path, that.path) &&
               Objects.equals(details, that.details);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(error, message, timestamp, path, details);
    }
    
    @Override
    public String toString() {
        return "ErrorResponse{" +
               "error='" + error + '\'' +
               ", message='" + message + '\'' +
               ", timestamp='" + timestamp + '\'' +
               ", path='" + path + '\'' +
               ", details=" + details +
               '}';
    }
}
