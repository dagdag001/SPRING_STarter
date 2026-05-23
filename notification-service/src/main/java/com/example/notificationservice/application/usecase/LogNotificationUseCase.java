package com.example.notificationservice.application.usecase;

import com.example.notificationservice.domain.entity.NotificationLog;
import com.example.notificationservice.domain.repository.NotificationLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case for logging all system events.
 * Receives events and stores them in the notification log.
 * 
 * Requirements: 1.2, 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7
 */
public class LogNotificationUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(LogNotificationUseCase.class);
    
    private final NotificationLogRepository notificationLogRepository;
    private final ObjectMapper objectMapper;
    
    public LogNotificationUseCase(NotificationLogRepository notificationLogRepository, ObjectMapper objectMapper) {
        if (notificationLogRepository == null) {
            throw new IllegalArgumentException("notificationLogRepository cannot be null");
        }
        if (objectMapper == null) {
            throw new IllegalArgumentException("objectMapper cannot be null");
        }
        this.notificationLogRepository = notificationLogRepository;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Logs a system event by creating a notification log entry.
     * 
     * @param eventType The type of the event
     * @param eventObject The event object to be serialized
     * @return The created notification log
     */
    public NotificationLog logEvent(String eventType, Object eventObject) {
        if (eventType == null || eventType.trim().isEmpty()) {
            throw new IllegalArgumentException("eventType cannot be null or empty");
        }
        if (eventObject == null) {
            throw new IllegalArgumentException("eventObject cannot be null");
        }
        
        try {
            // Serialize event object to JSON string
            String eventData = objectMapper.writeValueAsString(eventObject);
            
            // Create notification log
            NotificationLog notificationLog = new NotificationLog(eventType, eventData);
            
            // Save to repository
            NotificationLog savedLog = notificationLogRepository.save(notificationLog);
            
            logger.info("Logged {} event with logId: {}", eventType, savedLog.getLogId());
            
            return savedLog;
            
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize event object for eventType: {}", eventType, e);
            throw new RuntimeException("Failed to serialize event object", e);
        }
    }
}
