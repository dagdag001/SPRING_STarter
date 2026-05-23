package com.example.shared.exception;

/**
 * Base exception for all service-level exceptions.
 * All custom exceptions in the system should extend this class.
 * 
 * Requirements: 13.1, 14.6
 */
public class ServiceException extends RuntimeException {
    
    private final String errorCode;
    
    public ServiceException(String message) {
        super(message);
        this.errorCode = "SERVICE_ERROR";
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SERVICE_ERROR";
    }
    
    public ServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ServiceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
