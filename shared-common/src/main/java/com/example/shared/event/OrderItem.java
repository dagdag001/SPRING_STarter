package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents an item in an order.
 * Used in OrderCreatedPayload.
 * 
 * Requirements: 5.3
 */
public class OrderItem {
    
    @JsonProperty("productId")
    @NotBlank(message = "productId cannot be blank")
    private String productId;
    
    @JsonProperty("quantity")
    @NotNull(message = "quantity cannot be null")
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;
    
    @JsonProperty("price")
    @NotNull(message = "price cannot be null")
    @Min(value = 0, message = "price must be non-negative")
    private BigDecimal price;
    
    // Default constructor for Jackson
    public OrderItem() {
    }
    
    public OrderItem(String productId, Integer quantity, BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
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
