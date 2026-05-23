package com.example.orderservice.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value object representing an item in an order.
 * Immutable and contains product information and quantity.
 * 
 * Requirements: 1.1, 5.1
 */
public class OrderItem {
    
    private final String productId;
    private final Integer quantity;
    private final BigDecimal price;
    
    /**
     * Constructor for creating an order item.
     * 
     * @param productId The product identifier
     * @param quantity The quantity ordered
     * @param price The price per unit
     */
    public OrderItem(String productId, Integer quantity, BigDecimal price) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("productId cannot be null or empty");
        }
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("quantity must be at least 1");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price cannot be null or negative");
        }
        
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    /**
     * Calculates the total price for this item (price * quantity).
     * 
     * @return The total price
     */
    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(productId, orderItem.productId) &&
               Objects.equals(quantity, orderItem.quantity) &&
               Objects.equals(price, orderItem.price);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity, price);
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
               "productId='" + productId + '\'' +
               ", quantity=" + quantity +
               ", price=" + price +
               '}';
    }
}
