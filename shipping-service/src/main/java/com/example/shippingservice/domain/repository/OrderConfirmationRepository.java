package com.example.shippingservice.domain.repository;

import com.example.shippingservice.domain.entity.OrderConfirmation;

import java.util.Optional;

/**
 * Repository interface for OrderConfirmation entity.
 * Defines contract for order confirmation persistence operations.
 * 
 * Requirements: 1.1, 8.1, 22.4
 */
public interface OrderConfirmationRepository {
    
    /**
     * Saves an order confirmation to the repository.
     * 
     * @param orderConfirmation The order confirmation to save
     * @return The saved order confirmation
     */
    OrderConfirmation save(OrderConfirmation orderConfirmation);
    
    /**
     * Finds an order confirmation by order ID.
     * 
     * @param orderId The order ID
     * @return Optional containing the order confirmation if found
     */
    Optional<OrderConfirmation> findByOrderId(String orderId);
    
    /**
     * Checks if an order confirmation exists for the given order ID.
     * 
     * @param orderId The order ID
     * @return true if order confirmation exists, false otherwise
     */
    boolean existsByOrderId(String orderId);
}
