package com.example.notificationservice.infrastructure.config;

import com.example.notificationservice.application.usecase.LogNotificationUseCase;
import com.example.notificationservice.domain.repository.NotificationLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for application beans.
 * Wires up use cases with their dependencies.
 * 
 * Requirements: 1.3
 */
@Configuration
public class BeanConfig {
    
    @Bean
    public LogNotificationUseCase logNotificationUseCase(
            NotificationLogRepository notificationLogRepository,
            ObjectMapper objectMapper) {
        return new LogNotificationUseCase(notificationLogRepository, objectMapper);
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
