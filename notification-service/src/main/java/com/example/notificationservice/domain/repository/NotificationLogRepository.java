package com.example.notificationservice.domain.repository;

import com.example.notificationservice.domain.entity.NotificationLog;

/**
 * Repository interface for NotificationLog persistence operations.
 * Defines the contract for storing notification logs.
 * 
 * Requirements: 1.1, 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7
 */
public interface NotificationLogRepository {
    
    /**
     * Saves a notification log entry to the repository.
     * 
     * @param notificationLog The notification log to save
     * @return The saved notification log
     */
    NotificationLog save(NotificationLog notificationLog);
}
