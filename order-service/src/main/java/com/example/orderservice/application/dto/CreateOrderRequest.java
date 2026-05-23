package com.example.orderservice.application.dto;

import java.util.List;
import java.util.Objects;

/**
 * DTO for creating an order request.
 * 
 * Requirements: 1.2, 5.1
 */
public class CreateOrderRequest {
    
    private String customerId;
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
