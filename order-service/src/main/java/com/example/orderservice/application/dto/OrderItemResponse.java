package com.example.orderservice.application.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO for order item in order response.
 * 
 * Requirements: 1.2, 5.3
 */
public class OrderItemResponse {
    
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    
    public OrderItemResponse() {
    }
    
    public OrderItemResponse(String productId, Integer quantity, BigDecimal price) {
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
        OrderItemResponse that = (OrderItemResponse) o;
        return Objects.equals(productId, that.productId) &&
               Objects.equals(quantity, that.quantity) &&
               Objects.equals(price, that.price);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity, price);
    }
    
    @Override
    public String toString() {
        return "OrderItemResponse{" +
               "productId='" + productId + '\'' +
               ", quantity=" + quantity +
               ", price=" + price +
               '}';
    }
}
