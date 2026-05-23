package com.example.orderservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for OrderJpaEntity.
 * 
 * Requirements: 1.3, 11.2
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, String> {
}
