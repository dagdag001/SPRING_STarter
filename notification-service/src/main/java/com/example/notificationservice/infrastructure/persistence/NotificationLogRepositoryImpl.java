package com.example.notificationservice.infrastructure.persistence;

import com.example.notificationservice.domain.entity.NotificationLog;
import com.example.notificationservice.domain.repository.NotificationLogRepository;
import org.springframework.stereotype.Component;

/**
 * Implementation of NotificationLogRepository using JPA.
 * Converts between domain entities and JPA entities.
 * 
 * Requirements: 1.3, 11.6
 */
@Component
public class NotificationLogRepositoryImpl implements NotificationLogRepository {
    
    private final NotificationLogJpaRepository jpaRepository;
    
    public NotificationLogRepositoryImpl(NotificationLogJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public NotificationLog save(NotificationLog notificationLog) {
        NotificationLogJpaEntity jpaEntity = toJpaEntity(notificationLog);
        NotificationLogJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return toDomainEntity(savedEntity);
    }
    
    private NotificationLogJpaEntity toJpaEntity(NotificationLog notificationLog) {
        return new NotificationLogJpaEntity(
            notificationLog.getLogId(),
            notificationLog.getEventType(),
            notificationLog.getEventData(),
            notificationLog.getTimestamp()
        );
    }
    
    private NotificationLog toDomainEntity(NotificationLogJpaEntity jpaEntity) {
        return new NotificationLog(
            jpaEntity.getLogId(),
            jpaEntity.getEventType(),
            jpaEntity.getEventData(),
            jpaEntity.getTimestamp()
        );
    }
}
