package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Objects;

/**
 * Payload for StockReserved event.
 * Contains stock reservation information.
 * 
 * Requirements: 7.4
 */
public class StockReservedPayload {
    
    @JsonProperty("orderId")
    @NotBlank(message = "orderId cannot be blank")
    private String orderId;
    
    @JsonProperty("reservations")
    @NotEmpty(message = "reservations list cannot be empty")
    @Valid
    private List<StockReservation> reservations;
    
    // Default constructor for Jackson
    public StockReservedPayload() {
    }
    
    public StockReservedPayload(String orderId, List<StockReservation> reservations) {
        this.orderId = orderId;
        this.reservations = reservations;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public List<StockReservation> getReservations() {
        return reservations;
    }
    
    public void setReservations(List<StockReservation> reservations) {
        this.reservations = reservations;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockReservedPayload that = (StockReservedPayload) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(reservations, that.reservations);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId, reservations);
    }
    
    @Override
    public String toString() {
        return "StockReservedPayload{" +
               "orderId='" + orderId + '\'' +
               ", reservations=" + reservations +
               '}';
    }
}
