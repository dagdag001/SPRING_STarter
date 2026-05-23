package com.example.orderservice.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity for OrderItem persistence.
 * 
 * Requirements: 1.3, 11.2
 */
@Entity
@Table(name = "order_items")
public class OrderItemJpaEntity {
    
    @Id
    @Column(name = "order_item_id", nullable = false, length = 36)
    private String orderItemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;
    
    @Column(name = "product_id", nullable = false, length = 36)
    private String productId;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    public OrderItemJpaEntity() {
        this.orderItemId = UUID.randomUUID().toString();
    }
    
    public OrderItemJpaEntity(String productId, Integer quantity, BigDecimal price) {
        this.orderItemId = UUID.randomUUID().toString();
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
    
    public String getOrderItemId() {
        return orderItemId;
    }
    
    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }
    
    public OrderJpaEntity getOrder() {
        return order;
    }
    
    public void setOrder(OrderJpaEntity order) {
        this.order = order;
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
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemJpaEntity that = (OrderItemJpaEntity) o;
        return Objects.equals(orderItemId, that.orderItemId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderItemId);
    }
}
