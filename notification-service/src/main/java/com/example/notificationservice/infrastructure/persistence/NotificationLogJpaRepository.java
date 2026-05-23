package com.example.notificationservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for NotificationLogJpaEntity.
 * Provides CRUD operations for notification logs.
 * 
 * Requirements: 1.3, 11.6
 */
@Repository
public interface NotificationLogJpaRepository extends JpaRepository<NotificationLogJpaEntity, String> {
}
