package com.example.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Notification Service.
 * This service logs all system events for monitoring and auditing purposes.
 * 
 * Requirements: 2.6, 11.6, 12.1
 */
@SpringBootApplication
public class NotificationServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
