package com.example.notificationservice.domain.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a notification log entry.
 * Stores information about system events for monitoring and auditing.
 * 
 * Requirements: 1.1, 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7
 */
public class NotificationLog {
    
    private final String logId;
    private final String eventType;
    private final String eventData;
    private final Instant timestamp;
    
    /**
     * Constructor for creating a new notification log with generated ID and current timestamp.
     * 
     * @param eventType The type of the event being logged
     * @param eventData The event data as JSON string
     */
    public NotificationLog(String eventType, String eventData) {
        this(UUID.randomUUID().toString(), eventType, eventData, Instant.now());
    }
    
    /**
     * Constructor for creating a notification log with all fields specified.
     * Used primarily for reconstitution from persistence.
     * 
     * @param logId The unique identifier for this log entry
     * @param eventType The type of the event being logged
     * @param eventData The event data as JSON string
     * @param timestamp The timestamp when the event was logged
     */
    public NotificationLog(String logId, String eventType, String eventData, Instant timestamp) {
        if (logId == null || logId.trim().isEmpty()) {
            throw new IllegalArgumentException("logId cannot be null or empty");
        }
        if (eventType == null || eventType.trim().isEmpty()) {
            throw new IllegalArgumentException("eventType cannot be null or empty");
        }
        if (eventData == null || eventData.trim().isEmpty()) {
            throw new IllegalArgumentException("eventData cannot be null or empty");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp cannot be null");
        }
        
        this.logId = logId;
        this.eventType = eventType;
        this.eventData = eventData;
        this.timestamp = timestamp;
    }
    
    public String getLogId() {
        return logId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public String getEventData() {
        return eventData;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationLog that = (NotificationLog) o;
        return Objects.equals(logId, that.logId) &&
               Objects.equals(eventType, that.eventType) &&
               Objects.equals(eventData, that.eventData) &&
               Objects.equals(timestamp, that.timestamp);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(logId, eventType, eventData, timestamp);
    }
    
    @Override
    public String toString() {
        return "NotificationLog{" +
               "logId='" + logId + '\'' +
               ", eventType='" + eventType + '\'' +
               ", eventData='" + eventData + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
}
