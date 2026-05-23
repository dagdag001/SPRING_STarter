package com.example.shared.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Used for missing entities, records, or resources.
 * 
 * Requirements: 14.6
 */
public class NotFoundException extends ServiceException {
    
    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }
    
    public NotFoundException(String message, Throwable cause) {
        super("NOT_FOUND", message, cause);
    }
    
    public NotFoundException(String resourceType, String resourceId) {
        super("NOT_FOUND", String.format("%s with id '%s' not found", resourceType, resourceId));
    }
}
