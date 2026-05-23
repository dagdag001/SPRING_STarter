package com.example.orderservice.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JPA entity for Order persistence.
 * 
 * Requirements: 1.3, 11.2
 */
@Entity
@Table(name = "orders")
public class OrderJpaEntity {
    
    @Id
    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;
    
    @Column(name = "customer_id", nullable = false, length = 36)
    private String customerId;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItemJpaEntity> items = new ArrayList<>();
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    public OrderJpaEntity() {
    }
    
    public OrderJpaEntity(
            String orderId,
            String customerId,
            BigDecimal totalAmount,
            String status,
            Instant createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
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
    
    public List<OrderItemJpaEntity> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemJpaEntity> items) {
        this.items = items;
    }
    
    public void addItem(OrderItemJpaEntity item) {
        items.add(item);
        item.setOrder(this);
    }
    
    public void removeItem(OrderItemJpaEntity item) {
        items.remove(item);
        item.setOrder(null);
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
        OrderJpaEntity that = (OrderJpaEntity) o;
        return Objects.equals(orderId, that.orderId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
