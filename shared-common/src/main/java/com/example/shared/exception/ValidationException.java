package com.example.shared.exception;

/**
 * Exception thrown when validation fails.
 * Used for business rule validation and input validation errors.
 * 
 * Requirements: 14.6
 */
public class ValidationException extends ServiceException {
    
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super("VALIDATION_ERROR", message, cause);
    }
}
