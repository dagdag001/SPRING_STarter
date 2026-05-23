package com.example.orderservice.presentation.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * REST API response DTO for order.
 * 
 * Requirements: 1.4, 14.4
 */
public class OrderResponse {
    
    private String orderId;
    private String customerId;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private String status;
    private Instant createdAt;
    
    public OrderResponse() {
    }
    
    public OrderResponse(
            String orderId,
            String customerId,
            List<OrderItemResponse> items,
            BigDecimal totalAmount,
            String status,
            Instant createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
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
    
    public List<OrderItemResponse> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderResponse that = (OrderResponse) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(customerId, that.customerId) &&
               Objects.equals(items, that.items) &&
               Objects.equals(totalAmount, that.totalAmount) &&
               Objects.equals(status, that.status) &&
               Objects.equals(createdAt, that.createdAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId, customerId, items, totalAmount, status, createdAt);
    }
    
    @Override
    public String toString() {
        return "OrderResponse{" +
               "orderId='" + orderId + '\'' +
               ", customerId='" + customerId + '\'' +
               ", items=" + items +
               ", totalAmount=" + totalAmount +
               ", status='" + status + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}
