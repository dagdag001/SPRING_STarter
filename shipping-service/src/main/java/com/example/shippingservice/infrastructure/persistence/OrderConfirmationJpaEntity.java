package com.example.shippingservice.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity for OrderConfirmation.
 * 
 * Requirements: 1.3, 11.5
 */
@Entity
@Table(name = "order_confirmations")
public class OrderConfirmationJpaEntity {
    
    @Id
    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;
    
    @Column(name = "payment_confirmed", nullable = false)
    private boolean paymentConfirmed;
    
    @Column(name = "stock_confirmed", nullable = false)
    private boolean stockConfirmed;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Default constructor for JPA
    protected OrderConfirmationJpaEntity() {
    }
    
    public OrderConfirmationJpaEntity(String orderId, boolean paymentConfirmed, boolean stockConfirmed, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.paymentConfirmed = paymentConfirmed;
        this.stockConfirmed = stockConfirmed;
        this.updatedAt = updatedAt;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public boolean isPaymentConfirmed() {
        return paymentConfirmed;
    }
    
    public void setPaymentConfirmed(boolean paymentConfirmed) {
        this.paymentConfirmed = paymentConfirmed;
    }
    
    public boolean isStockConfirmed() {
        return stockConfirmed;
    }
    
    public void setStockConfirmed(boolean stockConfirmed) {
        this.stockConfirmed = stockConfirmed;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderConfirmationJpaEntity that = (OrderConfirmationJpaEntity) o;
        return Objects.equals(orderId, that.orderId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
