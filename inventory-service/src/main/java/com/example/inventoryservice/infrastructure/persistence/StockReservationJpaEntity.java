package com.example.inventoryservice.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity for StockReservation.
 * 
 * Requirements: 1.3, 11.4
 */
@Entity
@Table(name = "stock_reservations")
public class StockReservationJpaEntity {
    
    @Id
    @Column(name = "reservation_id", length = 36, nullable = false)
    private String reservationId;
    
    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;
    
    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;
    
    @Column(name = "quantity", nullable = false)
    private int quantity;
    
    @Column(name = "reserved_at", nullable = false)
    private LocalDateTime reservedAt;
    
    // Default constructor for JPA
    protected StockReservationJpaEntity() {
    }
    
    public StockReservationJpaEntity(String reservationId, String orderId, String productId, int quantity, LocalDateTime reservedAt) {
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.reservedAt = reservedAt;
    }
    
    public String getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public LocalDateTime getReservedAt() {
        return reservedAt;
    }
    
    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockReservationJpaEntity that = (StockReservationJpaEntity) o;
        return Objects.equals(reservationId, that.reservationId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }
}
