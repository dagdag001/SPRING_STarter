package com.example.inventoryservice.domain.repository;

import com.example.inventoryservice.domain.entity.StockReservation;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for StockReservation entity.
 * Defines contract for stock reservation persistence operations.
 * 
 * Requirements: 1.1, 7.1
 */
public interface StockReservationRepository {
    
    /**
     * Saves a stock reservation to the repository.
     * 
     * @param reservation The stock reservation to save
     * @return The saved stock reservation
     */
    StockReservation save(StockReservation reservation);
    
    /**
     * Finds a stock reservation by its ID.
     * 
     * @param reservationId The reservation ID
     * @return Optional containing the reservation if found
     */
    Optional<StockReservation> findById(String reservationId);
    
    /**
     * Finds all stock reservations for a given order.
     * 
     * @param orderId The order ID
     * @return List of stock reservations for the order
     */
    List<StockReservation> findByOrderId(String orderId);
    
    /**
     * Checks if a reservation exists for the given order ID.
     * 
     * @param orderId The order ID
     * @return true if reservation exists, false otherwise
     */
    boolean existsByOrderId(String orderId);
}
