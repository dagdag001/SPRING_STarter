package com.example.orderservice.domain.entity;

import com.example.orderservice.domain.valueobject.OrderItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing an order in the system.
 * Contains order information including items, customer, and total amount.
 * 
 * Requirements: 1.1, 5.1
 */
public class Order {
    
    private final String orderId;
    private final String customerId;
    private final List<OrderItem> items;
    private final BigDecimal totalAmount;
    private final String status;
    private final Instant createdAt;
    
    /**
     * Constructor for creating a new order.
     * 
     * @param customerId The customer identifier
     * @param items The list of order items
     */
    public Order(String customerId, List<OrderItem> items) {
        this(
            UUID.randomUUID().toString(),
            customerId,
            items,
            calculateTotalAmount(items),
            "CREATED",
            Instant.now()
        );
    }
    
    /**
     * Constructor for reconstituting an order from persistence.
     * 
     * @param orderId The unique identifier
     * @param customerId The customer identifier
     * @param items The list of order items
     * @param totalAmount The total amount of the order
     * @param status The order status
     * @param createdAt The timestamp when the order was created
     */
    public Order(
            String orderId,
            String customerId,
            List<OrderItem> items,
            BigDecimal totalAmount,
            String status,
            Instant createdAt) {
        
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("orderId cannot be null or empty");
        }
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("customerId cannot be null or empty");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items list cannot be null or empty");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("totalAmount cannot be null or negative");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("status cannot be null or empty");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt cannot be null");
        }
        
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    /**
     * Calculates the total amount from order items.
     * 
     * @param items The list of order items
     * @return The total amount
     */
    private static BigDecimal calculateTotalAmount(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
    
    @Override
    public String toString() {
        return "Order{" +
               "orderId='" + orderId + '\'' +
               ", customerId='" + customerId + '\'' +
               ", items=" + items +
               ", totalAmount=" + totalAmount +
               ", status='" + status + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}
