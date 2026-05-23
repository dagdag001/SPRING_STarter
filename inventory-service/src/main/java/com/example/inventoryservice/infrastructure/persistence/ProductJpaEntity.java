package com.example.inventoryservice.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity for Product.
 * 
 * Requirements: 1.3, 11.4
 */
@Entity
@Table(name = "products")
public class ProductJpaEntity {
    
    @Id
    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;
    
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Default constructor for JPA
    protected ProductJpaEntity() {
    }
    
    public ProductJpaEntity(String productId, String name, int stockQuantity, LocalDateTime updatedAt) {
        this.productId = productId;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.updatedAt = updatedAt;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductJpaEntity that = (ProductJpaEntity) o;
        return Objects.equals(productId, that.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
