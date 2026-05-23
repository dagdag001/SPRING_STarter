package com.example.shippingservice.domain.repository;

import com.example.shippingservice.domain.entity.Shipment;

import java.util.Optional;

/**
 * Repository interface for Shipment entity.
 * Defines contract for shipment persistence operations.
 * 
 * Requirements: 1.1, 8.1, 22.4
 */
public interface ShipmentRepository {
    
    /**
     * Saves a shipment to the repository.
     * 
     * @param shipment The shipment to save
     * @return The saved shipment
     */
    Shipment save(Shipment shipment);
    
    /**
     * Finds a shipment by its ID.
     * 
     * @param shipmentId The shipment ID
     * @return Optional containing the shipment if found
     */
    Optional<Shipment> findById(String shipmentId);
    
    /**
     * Finds a shipment by order ID.
     * 
     * @param orderId The order ID
     * @return Optional containing the shipment if found
     */
    Optional<Shipment> findByOrderId(String orderId);
    
    /**
     * Checks if a shipment exists for the given order ID.
     * 
     * @param orderId The order ID
     * @return true if shipment exists, false otherwise
     */
    boolean existsByOrderId(String orderId);
}
