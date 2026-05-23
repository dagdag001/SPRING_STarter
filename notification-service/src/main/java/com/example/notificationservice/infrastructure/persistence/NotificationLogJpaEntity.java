package com.example.notificationservice.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * JPA entity for NotificationLog persistence.
 * Maps to the notification_logs table in the database.
 * 
 * Requirements: 1.3, 11.6
 */
@Entity
@Table(name = "notification_logs", indexes = {
    @Index(name = "idx_logs_event_type", columnList = "event_type"),
    @Index(name = "idx_logs_timestamp", columnList = "timestamp")
})
public class NotificationLogJpaEntity {
    
    @Id
    @Column(name = "log_id", nullable = false, length = 36)
    private String logId;
    
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;
    
    @Column(name = "event_data", nullable = false, columnDefinition = "TEXT")
    private String eventData;
    
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
    
    protected NotificationLogJpaEntity() {
        // Required by JPA
    }
    
    public NotificationLogJpaEntity(String logId, String eventType, String eventData, Instant timestamp) {
        this.logId = logId;
        this.eventType = eventType;
        this.eventData = eventData;
        this.timestamp = timestamp;
    }
    
    public String getLogId() {
        return logId;
    }
    
    public void setLogId(String logId) {
        this.logId = logId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getEventData() {
        return eventData;
    }
    
    public void setEventData(String eventData) {
        this.eventData = eventData;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationLogJpaEntity that = (NotificationLogJpaEntity) o;
        return Objects.equals(logId, that.logId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(logId);
    }
}
