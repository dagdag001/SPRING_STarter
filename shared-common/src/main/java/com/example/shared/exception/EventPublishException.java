package com.example.shared.exception;

/**
 * Exception thrown when event publishing fails.
 * Used for RabbitMQ publishing errors and message broker issues.
 * 
 * Requirements: 13.1
 */
public class EventPublishException extends ServiceException {
    
    public EventPublishException(String message) {
        super("EVENT_PUBLISH_ERROR", message);
    }
    
    public EventPublishException(String message, Throwable cause) {
        super("EVENT_PUBLISH_ERROR", message, cause);
    }
}
