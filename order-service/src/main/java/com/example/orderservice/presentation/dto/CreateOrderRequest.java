package com.example.orderservice.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Objects;

/**
 * REST API request DTO for creating an order.
 * 
 * Requirements: 1.4, 14.3, 14.5
 */
public class CreateOrderRequest {
    
    @NotBlank(message = "Customer ID cannot be blank")
    private String customerId;
    
    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<OrderItemRequest> items;
    
    public CreateOrderRequest() {
    }
    
    public CreateOrderRequest(String customerId, List<OrderItemRequest> items) {
        this.customerId = customerId;
        this.items = items;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public List<OrderItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateOrderRequest that = (CreateOrderRequest) o;
        return Objects.equals(customerId, that.customerId) &&
               Objects.equals(items, that.items);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(customerId, items);
    }
    
    @Override
    public String toString() {
        return "CreateOrderRequest{" +
               "customerId='" + customerId + '\'' +
               ", items=" + items +
               '}';
    }
}
