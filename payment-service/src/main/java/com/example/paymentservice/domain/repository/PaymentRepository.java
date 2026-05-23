package com.example.paymentservice.domain.repository;

import com.example.paymentservice.domain.entity.Payment;

import java.util.Optional;

/**
 * Repository interface for Payment entity.
 * Defines contract for payment persistence operations.
 * 
 * Requirements: 1.1, 6.1
 */
public interface PaymentRepository {
    
    /**
     * Saves a payment to the repository.
     * 
     * @param payment The payment to save
     * @return The saved payment
     */
    Payment save(Payment payment);
    
    /**
     * Finds a payment by its ID.
     * 
     * @param paymentId The payment ID
     * @return Optional containing the payment if found
     */
    Optional<Payment> findById(String paymentId);
    
    /**
     * Finds a payment by order ID.
     * 
     * @param orderId The order ID
     * @return Optional containing the payment if found
     */
    Optional<Payment> findByOrderId(String orderId);
    
    /**
     * Checks if a payment exists for the given order ID.
     * 
     * @param orderId The order ID
     * @return true if payment exists, false otherwise
     */
    boolean existsByOrderId(String orderId);
}
