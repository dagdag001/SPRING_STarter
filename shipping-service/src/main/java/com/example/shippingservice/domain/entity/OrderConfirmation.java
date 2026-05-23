package com.example.shippingservice.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing order confirmation state.
 * Tracks payment and stock confirmations for event correlation.
 * 
 * Requirements: 1.1, 8.1, 22.4
 */
public class OrderConfirmation {
    
    private final String orderId;
    private final boolean paymentConfirmed;
    private final boolean stockConfirmed;
    private final LocalDateTime updatedAt;
    
    public OrderConfirmation(String orderId, boolean paymentConfirmed, boolean stockConfirmed, LocalDateTime updatedAt) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("orderId cannot be null or empty");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("updatedAt cannot be null");
        }
        
        this.orderId = orderId;
        this.paymentConfirmed = paymentConfirmed;
        this.stockConfirmed = stockConfirmed;
        this.updatedAt = updatedAt;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public boolean isPaymentConfirmed() {
        return paymentConfirmed;
    }
    
    public boolean isStockConfirmed() {
        return stockConfirmed;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Checks if both payment and stock are confirmed.
     * 
     * @return true if both confirmations are received
     */
    public boolean isBothConfirmed() {
        return paymentConfirmed && stockConfirmed;
    }
    
    /**
     * Creates a new OrderConfirmation with payment confirmed.
     * 
     * @return new OrderConfirmation with payment confirmed
     */
    public OrderConfirmation withPaymentConfirmed() {
        return new OrderConfirmation(orderId, true, stockConfirmed, LocalDateTime.now());
    }
    
    /**
     * Creates a new OrderConfirmation with stock confirmed.
     * 
     * @return new OrderConfirmation with stock confirmed
     */
    public OrderConfirmation withStockConfirmed() {
        return new OrderConfirmation(orderId, paymentConfirmed, true, LocalDateTime.now());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderConfirmation that = (OrderConfirmation) o;
        return paymentConfirmed == that.paymentConfirmed &&
               stockConfirmed == that.stockConfirmed &&
               Objects.equals(orderId, that.orderId) &&
               Objects.equals(updatedAt, that.updatedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId, paymentConfirmed, stockConfirmed, updatedAt);
    }
    
    @Override
    public String toString() {
        return "OrderConfirmation{" +
               "orderId='" + orderId + '\'' +
               ", paymentConfirmed=" + paymentConfirmed +
               ", stockConfirmed=" + stockConfirmed +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
