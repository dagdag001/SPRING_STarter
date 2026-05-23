package com.example.inventoryservice.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a stock reservation for an order.
 * 
 * Requirements: 1.1, 7.1
 */
public class StockReservation {
    
    private final String reservationId;
    private final String orderId;
    private final String productId;
    private final int quantity;
    private final LocalDateTime reservedAt;
    
    public StockReservation(String reservationId, String orderId, String productId, int quantity, LocalDateTime reservedAt) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new IllegalArgumentException("reservationId cannot be null or empty");
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("orderId cannot be null or empty");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("productId cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (reservedAt == null) {
            throw new IllegalArgumentException("reservedAt cannot be null");
        }
        
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.reservedAt = reservedAt;
    }
    
    public String getReservationId() {
        return reservationId;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public LocalDateTime getReservedAt() {
        return reservedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockReservation that = (StockReservation) o;
        return quantity == that.quantity &&
               Objects.equals(reservationId, that.reservationId) &&
               Objects.equals(orderId, that.orderId) &&
               Objects.equals(productId, that.productId) &&
               Objects.equals(reservedAt, that.reservedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(reservationId, orderId, productId, quantity, reservedAt);
    }
    
    @Override
    public String toString() {
        return "StockReservation{" +
               "reservationId='" + reservationId + '\'' +
               ", orderId='" + orderId + '\'' +
               ", productId='" + productId + '\'' +
               ", quantity=" + quantity +
               ", reservedAt=" + reservedAt +
               '}';
    }
}
