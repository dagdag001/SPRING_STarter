package com.example.inventoryservice.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a product in inventory.
 * 
 * Requirements: 1.1, 7.1
 */
public class Product {
    
    private final String productId;
    private final String name;
    private int stockQuantity;
    private final LocalDateTime updatedAt;
    
    public Product(String productId, String name, int stockQuantity, LocalDateTime updatedAt) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("productId cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("stockQuantity cannot be negative");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("updatedAt cannot be null");
        }
        
        this.productId = productId;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.updatedAt = updatedAt;
    }
    
    /**
     * Checks if sufficient stock is available.
     * 
     * @param quantity The quantity to check
     * @return true if sufficient stock exists, false otherwise
     */
    public boolean hasSufficientStock(int quantity) {
        return stockQuantity >= quantity;
    }
    
    /**
     * Reserves stock by reducing the stock quantity.
     * 
     * @param quantity The quantity to reserve
     * @throws IllegalStateException if insufficient stock
     */
    public void reserveStock(int quantity) {
        if (!hasSufficientStock(quantity)) {
            throw new IllegalStateException("Insufficient stock for product: " + productId);
        }
        this.stockQuantity -= quantity;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public String getName() {
        return name;
    }
    
    public int getStockQuantity() {
        return stockQuantity;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return stockQuantity == product.stockQuantity &&
               Objects.equals(productId, product.productId) &&
               Objects.equals(name, product.name) &&
               Objects.equals(updatedAt, product.updatedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId, name, stockQuantity, updatedAt);
    }
    
    @Override
    public String toString() {
        return "Product{" +
               "productId='" + productId + '\'' +
               ", name='" + name + '\'' +
               ", stockQuantity=" + stockQuantity +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
