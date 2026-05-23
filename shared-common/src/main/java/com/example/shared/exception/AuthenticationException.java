package com.example.shared.exception;

/**
 * Exception thrown when authentication fails.
 * Used for invalid credentials, expired tokens, and unauthorized access.
 * 
 * Requirements: 14.6
 */
public class AuthenticationException extends ServiceException {
    
    public AuthenticationException(String message) {
        super("AUTHENTICATION_ERROR", message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super("AUTHENTICATION_ERROR", message, cause);
    }
}
