package com.example.orderservice.domain.repository;

import com.example.orderservice.domain.entity.Order;

import java.util.Optional;

/**
 * Repository interface for Order entity.
 * Defines contract for order persistence operations.
 * 
 * Requirements: 1.1, 5.1
 */
public interface OrderRepository {
    
    /**
     * Saves an order to the repository.
     * 
     * @param order The order to save
     * @return The saved order
     */
    Order save(Order order);
    
    /**
     * Finds an order by its identifier.
     * 
     * @param orderId The order identifier
     * @return An Optional containing the order if found, empty otherwise
     */
    Optional<Order> findById(String orderId);
    
    /**
     * Checks if an order exists by its identifier.
     * 
     * @param orderId The order identifier
     * @return true if the order exists, false otherwise
     */
    boolean existsById(String orderId);
}
