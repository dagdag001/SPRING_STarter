package com.example.shippingservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for ShipmentJpaEntity.
 * 
 * Requirements: 1.3, 11.5
 */
@Repository
public interface ShipmentJpaRepository extends JpaRepository<ShipmentJpaEntity, String> {
    
    /**
     * Finds a shipment by order ID.
     * 
     * @param orderId The order ID
     * @return Optional containing the shipment if found
     */
    Optional<ShipmentJpaEntity> findByOrderId(String orderId);
    
    /**
     * Checks if a shipment exists for the given order ID.
     * 
     * @param orderId The order ID
     * @return true if shipment exists, false otherwise
     */
    boolean existsByOrderId(String orderId);
}
