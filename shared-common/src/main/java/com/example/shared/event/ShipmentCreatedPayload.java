package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Payload for ShipmentCreated event.
 * Contains shipment creation information.
 * 
 * Requirements: 8.3
 */
public class ShipmentCreatedPayload {
    
    @JsonProperty("shipmentId")
    @NotBlank(message = "shipmentId cannot be blank")
    private String shipmentId;
    
    @JsonProperty("orderId")
    @NotBlank(message = "orderId cannot be blank")
    private String orderId;
    
    @JsonProperty("estimatedDeliveryDate")
    @NotNull(message = "estimatedDeliveryDate cannot be null")
    private String estimatedDeliveryDate;
    
    // Default constructor for Jackson
    public ShipmentCreatedPayload() {
    }
    
    public ShipmentCreatedPayload(String shipmentId, String orderId, String estimatedDeliveryDate) {
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
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
    
    public String getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }
    
    public void setEstimatedDeliveryDate(String estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipmentCreatedPayload that = (ShipmentCreatedPayload) o;
        return Objects.equals(shipmentId, that.shipmentId) &&
               Objects.equals(orderId, that.orderId) &&
               Objects.equals(estimatedDeliveryDate, that.estimatedDeliveryDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(shipmentId, orderId, estimatedDeliveryDate);
    }
    
    @Override
    public String toString() {
        return "ShipmentCreatedPayload{" +
               "shipmentId='" + shipmentId + '\'' +
               ", orderId='" + orderId + '\'' +
               ", estimatedDeliveryDate='" + estimatedDeliveryDate + '\'' +
               '}';
    }
}
