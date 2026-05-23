package com.example.shippingservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for OrderConfirmationJpaEntity.
 * 
 * Requirements: 1.3, 11.5
 */
@Repository
public interface OrderConfirmationJpaRepository extends JpaRepository<OrderConfirmationJpaEntity, String> {
    
    /**
     * Checks if an order confirmation exists for the given order ID.
     * 
     * @param orderId The order ID
     * @return true if order confirmation exists, false otherwise
     */
    boolean existsByOrderId(String orderId);
}
