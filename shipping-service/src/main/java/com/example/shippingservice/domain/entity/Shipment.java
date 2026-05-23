package com.example.shippingservice.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a shipment.
 * 
 * Requirements: 1.1, 8.1, 22.4
 */
public class Shipment {
    
    private final String shipmentId;
    private final String orderId;
    private final LocalDate estimatedDeliveryDate;
    private final String status;
    private final LocalDateTime createdAt;
    
    public Shipment(String shipmentId, String orderId, LocalDate estimatedDeliveryDate, String status, LocalDateTime createdAt) {
        if (shipmentId == null || shipmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("shipmentId cannot be null or empty");
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("orderId cannot be null or empty");
        }
        if (estimatedDeliveryDate == null) {
            throw new IllegalArgumentException("estimatedDeliveryDate cannot be null");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("status cannot be null or empty");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt cannot be null");
        }
        
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    public String getShipmentId() {
        return shipmentId;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public LocalDate getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shipment shipment = (Shipment) o;
        return Objects.equals(shipmentId, shipment.shipmentId) &&
               Objects.equals(orderId, shipment.orderId) &&
               Objects.equals(estimatedDeliveryDate, shipment.estimatedDeliveryDate) &&
               Objects.equals(status, shipment.status) &&
               Objects.equals(createdAt, shipment.createdAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(shipmentId, orderId, estimatedDeliveryDate, status, createdAt);
    }
    
    @Override
    public String toString() {
        return "Shipment{" +
               "shipmentId='" + shipmentId + '\'' +
               ", orderId='" + orderId + '\'' +
               ", estimatedDeliveryDate=" + estimatedDeliveryDate +
               ", status='" + status + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}
