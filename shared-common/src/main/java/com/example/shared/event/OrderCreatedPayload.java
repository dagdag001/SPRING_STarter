package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Payload for OrderCreated event.
 * Contains order creation information.
 * 
 * Requirements: 5.3
 */
public class OrderCreatedPayload {
    
    @JsonProperty("orderId")
    @NotBlank(message = "orderId cannot be blank")
    private String orderId;
    
    @JsonProperty("customerId")
    @NotBlank(message = "customerId cannot be blank")
    private String customerId;
    
    @JsonProperty("items")
    @NotEmpty(message = "items list cannot be empty")
    @Valid
    private List<OrderItem> items;
    
    @JsonProperty("totalAmount")
    @NotNull(message = "totalAmount cannot be null")
    @Min(value = 0, message = "totalAmount must be non-negative")
    private BigDecimal totalAmount;
    
    // Default constructor for Jackson
    public OrderCreatedPayload() {
    }
    
    public OrderCreatedPayload(String orderId, String customerId, List<OrderItem> items, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = totalAmount;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderCreatedPayload that = (OrderCreatedPayload) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(customerId, that.customerId) &&
               Objects.equals(items, that.items) &&
               Objects.equals(totalAmount, that.totalAmount);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId, customerId, items, totalAmount);
    }
    
    @Override
    public String toString() {
        return "OrderCreatedPayload{" +
               "orderId='" + orderId + '\'' +
               ", customerId='" + customerId + '\'' +
               ", items=" + items +
               ", totalAmount=" + totalAmount +
               '}';
    }
}
