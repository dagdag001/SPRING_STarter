package com.example.shippingservice.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity for Shipment.
 * 
 * Requirements: 1.3, 11.5
 */
@Entity
@Table(name = "shipments")
public class ShipmentJpaEntity {
    
    @Id
    @Column(name = "shipment_id", length = 36, nullable = false)
    private String shipmentId;
    
    @Column(name = "order_id", length = 36, nullable = false, unique = true)
    private String orderId;
    
    @Column(name = "estimated_delivery_date", nullable = false)
    private LocalDate estimatedDeliveryDate;
    
    @Column(name = "status", length = 20, nullable = false)
    private String status;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Default constructor for JPA
    protected ShipmentJpaEntity() {
    }
    
    public ShipmentJpaEntity(String shipmentId, String orderId, LocalDate estimatedDeliveryDate, String status, LocalDateTime createdAt) {
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    public String getShipmentId() {
        return shipmentId;
    }
    
    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public LocalDate getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }
    
    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipmentJpaEntity that = (ShipmentJpaEntity) o;
        return Objects.equals(shipmentId, that.shipmentId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(shipmentId);
    }
}
