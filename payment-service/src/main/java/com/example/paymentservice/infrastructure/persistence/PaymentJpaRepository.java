package com.example.paymentservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for PaymentJpaEntity.
 * 
 * Requirements: 1.3, 11.3
 */
@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, String> {
    
    /**
     * Finds a payment by order ID.
     * 
     * @param orderId The order ID
     * @return Optional containing the payment if found
     */
    Optional<PaymentJpaEntity> findByOrderId(String orderId);
    
    /**
     * Checks if a payment exists for the given order ID.
     * 
     * @param orderId The order ID
     * @return true if payment exists, false otherwise
     */
    boolean existsByOrderId(String orderId);
}
