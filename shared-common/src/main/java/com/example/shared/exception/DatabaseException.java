package com.example.shared.exception;

/**
 * Exception thrown when database operations fail.
 * Used for connection errors, query failures, and persistence issues.
 * 
 * Requirements: 13.1
 */
public class DatabaseException extends ServiceException {
    
    public DatabaseException(String message) {
        super("DATABASE_ERROR", message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super("DATABASE_ERROR", message, cause);
    }
}
