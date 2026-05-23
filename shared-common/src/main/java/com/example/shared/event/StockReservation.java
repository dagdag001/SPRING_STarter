package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Represents a stock reservation for a product.
 * Used in StockReservedPayload.
 * 
 * Requirements: 7.4
 */
public class StockReservation {
    
    @JsonProperty("productId")
    @NotBlank(message = "productId cannot be blank")
    private String productId;
    
    @JsonProperty("quantity")
    @NotNull(message = "quantity cannot be null")
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;
    
    // Default constructor for Jackson
    public StockReservation() {
    }
    
    public StockReservation(String productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockReservation that = (StockReservation) o;
        return Objects.equals(productId, that.productId) &&
               Objects.equals(quantity, that.quantity);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity);
    }
    
    @Override
    public String toString() {
        return "StockReservation{" +
               "productId='" + productId + '\'' +
               ", quantity=" + quantity +
               '}';
    }
}
