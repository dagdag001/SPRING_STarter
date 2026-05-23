package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Objects;

/**
 * Payload for StockFailed event.
 * Contains stock failure information.
 * 
 * Requirements: 7.5
 */
public class StockFailedPayload {
    
    @JsonProperty("orderId")
    @NotBlank(message = "orderId cannot be blank")
    private String orderId;
    
    @JsonProperty("unavailableProducts")
    @NotEmpty(message = "unavailableProducts list cannot be empty")
    private List<String> unavailableProducts;
    
    // Default constructor for Jackson
    public StockFailedPayload() {
    }
    
    public StockFailedPayload(String orderId, List<String> unavailableProducts) {
        this.orderId = orderId;
        this.unavailableProducts = unavailableProducts;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public List<String> getUnavailableProducts() {
        return unavailableProducts;
    }
    
    public void setUnavailableProducts(List<String> unavailableProducts) {
        this.unavailableProducts = unavailableProducts;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockFailedPayload that = (StockFailedPayload) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(unavailableProducts, that.unavailableProducts);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId, unavailableProducts);
    }
    
    @Override
    public String toString() {
        return "StockFailedPayload{" +
               "orderId='" + orderId + '\'' +
               ", unavailableProducts=" + unavailableProducts +
               '}';
    }
}
