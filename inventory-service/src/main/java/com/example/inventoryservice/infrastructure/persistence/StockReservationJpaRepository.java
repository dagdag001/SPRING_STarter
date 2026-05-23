package com.example.inventoryservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for StockReservationJpaEntity.
 * 
 * Requirements: 1.3, 11.4
 */
@Repository
public interface StockReservationJpaRepository extends JpaRepository<StockReservationJpaEntity, String> {
    
    /**
     * Finds all stock reservations for a given order.
     * 
     * @param orderId The order ID
     * @return List of stock reservations
     */
    List<StockReservationJpaEntity> findByOrderId(String orderId);
    
    /**
     * Checks if a reservation exists for the given order ID.
     * 
     * @param orderId The order ID
     * @return true if exists, false otherwise
     */
    boolean existsByOrderId(String orderId);
}
