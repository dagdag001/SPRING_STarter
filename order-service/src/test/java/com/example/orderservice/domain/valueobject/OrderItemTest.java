package com.example.orderservice.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OrderItem value object.
 */
class OrderItemTest {
    
    @Test
    void shouldCreateOrderItemWithValidData() {
        // Given
        String productId = "PROD-123";
        Integer quantity = 5;
        BigDecimal price = new BigDecimal("19.99");
        
        // When
        OrderItem orderItem = new OrderItem(productId, quantity, price);
        
        // Then
        assertNotNull(orderItem);
        assertEquals(productId, orderItem.getProductId());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(price, orderItem.getPrice());
    }
    
    @Test
    void shouldCalculateTotalPriceCorrectly() {
        // Given
        OrderItem orderItem = new OrderItem("PROD-123", 3, new BigDecimal("10.00"));
        
        // When
        BigDecimal totalPrice = orderItem.getTotalPrice();
        
        // Then
        assertEquals(new BigDecimal("30.00"), totalPrice);
    }
    
    @Test
    void shouldThrowExceptionWhenProductIdIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem(null, 1, BigDecimal.TEN)
        );
        assertEquals("productId cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenProductIdIsEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem("", 1, BigDecimal.TEN)
        );
        assertEquals("productId cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenQuantityIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem("PROD-123", null, BigDecimal.TEN)
        );
        assertEquals("quantity must be at least 1", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem("PROD-123", 0, BigDecimal.TEN)
        );
        assertEquals("quantity must be at least 1", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem("PROD-123", -1, BigDecimal.TEN)
        );
        assertEquals("quantity must be at least 1", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem("PROD-123", 1, null)
        );
        assertEquals("price cannot be null or negative", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem("PROD-123", 1, new BigDecimal("-10.00"))
        );
        assertEquals("price cannot be null or negative", exception.getMessage());
    }
    
    @Test
    void shouldAcceptZeroPrice() {
        // Given & When
        OrderItem orderItem = new OrderItem("PROD-123", 1, BigDecimal.ZERO);
        
        // Then
        assertNotNull(orderItem);
        assertEquals(BigDecimal.ZERO, orderItem.getPrice());
    }
    
    @Test
    void shouldBeEqualWhenAllFieldsMatch() {
        // Given
        OrderItem item1 = new OrderItem("PROD-123", 2, new BigDecimal("15.00"));
        OrderItem item2 = new OrderItem("PROD-123", 2, new BigDecimal("15.00"));
        
        // When & Then
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenProductIdDiffers() {
        // Given
        OrderItem item1 = new OrderItem("PROD-123", 2, new BigDecimal("15.00"));
        OrderItem item2 = new OrderItem("PROD-456", 2, new BigDecimal("15.00"));
        
        // When & Then
        assertNotEquals(item1, item2);
    }
    
    @Test
    void shouldNotBeEqualWhenQuantityDiffers() {
        // Given
        OrderItem item1 = new OrderItem("PROD-123", 2, new BigDecimal("15.00"));
        OrderItem item2 = new OrderItem("PROD-123", 3, new BigDecimal("15.00"));
        
        // When & Then
        assertNotEquals(item1, item2);
    }
    
    @Test
    void shouldNotBeEqualWhenPriceDiffers() {
        // Given
        OrderItem item1 = new OrderItem("PROD-123", 2, new BigDecimal("15.00"));
        OrderItem item2 = new OrderItem("PROD-123", 2, new BigDecimal("20.00"));
        
        // When & Then
        assertNotEquals(item1, item2);
    }
}
