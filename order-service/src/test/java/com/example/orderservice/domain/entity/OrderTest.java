package com.example.orderservice.domain.entity;

import com.example.orderservice.domain.valueobject.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Order domain entity.
 */
class OrderTest {
    
    @Test
    void shouldCreateOrderWithValidData() {
        // Given
        String customerId = "CUST-123";
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 2, new BigDecimal("10.00")),
            new OrderItem("PROD-2", 1, new BigDecimal("15.00"))
        );
        
        // When
        Order order = new Order(customerId, items);
        
        // Then
        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(2, order.getItems().size());
        assertEquals(new BigDecimal("35.00"), order.getTotalAmount());
        assertEquals("CREATED", order.getStatus());
        assertNotNull(order.getCreatedAt());
    }
    
    @Test
    void shouldCalculateTotalAmountCorrectly() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 3, new BigDecimal("10.00")),
            new OrderItem("PROD-2", 2, new BigDecimal("25.50"))
        );
        
        // When
        Order order = new Order("CUST-123", items);
        
        // Then
        assertEquals(new BigDecimal("81.00"), order.getTotalAmount());
    }
    
    @Test
    void shouldCreateOrderWithFullConstructor() {
        // Given
        String orderId = "ORDER-123";
        String customerId = "CUST-123";
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        );
        BigDecimal totalAmount = new BigDecimal("10.00");
        String status = "COMPLETED";
        Instant createdAt = Instant.now();
        
        // When
        Order order = new Order(orderId, customerId, items, totalAmount, status, createdAt);
        
        // Then
        assertEquals(orderId, order.getOrderId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(1, order.getItems().size());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(status, order.getStatus());
        assertEquals(createdAt, order.getCreatedAt());
    }
    
    @Test
    void shouldReturnUnmodifiableItemsList() {
        // Given
        List<OrderItem> items = new ArrayList<>(Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        ));
        Order order = new Order("CUST-123", items);
        
        // When & Then
        assertThrows(UnsupportedOperationException.class, () -> {
            order.getItems().add(new OrderItem("PROD-2", 1, new BigDecimal("5.00")));
        });
    }
    
    @Test
    void shouldThrowExceptionWhenOrderIdIsNull() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        );
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order(null, "CUST-123", items, BigDecimal.TEN, "CREATED", Instant.now())
        );
        assertEquals("orderId cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenOrderIdIsEmpty() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        );
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order("", "CUST-123", items, BigDecimal.TEN, "CREATED", Instant.now())
        );
        assertEquals("orderId cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerIdIsNull() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        );
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order(null, items)
        );
        assertEquals("customerId cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerIdIsEmpty() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        );
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order("", items)
        );
        assertEquals("customerId cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenItemsListIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order("CUST-123", null)
        );
        assertEquals("items list cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenItemsListIsEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order("CUST-123", new ArrayList<>())
        );
        assertEquals("items list cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenTotalAmountIsNull() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        );
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order("ORDER-123", "CUST-123", items, null, "CREATED", Instant.now())
        );
        assertEquals("totalAmount cannot be null or negative", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenTotalAmountIsNegative() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        );
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order("ORDER-123", "CUST-123", items, new BigDecimal("-10.00"), "CREATED", Instant.now())
        );
        assertEquals("totalAmount cannot be null or negative", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenStatusIsNull() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        );
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order("ORDER-123", "CUST-123", items, BigDecimal.TEN, null, Instant.now())
        );
        assertEquals("status cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenStatusIsEmpty() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        );
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order("ORDER-123", "CUST-123", items, BigDecimal.TEN, "", Instant.now())
        );
        assertEquals("status cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenCreatedAtIsNull() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        );
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order("ORDER-123", "CUST-123", items, BigDecimal.TEN, "CREATED", null)
        );
        assertEquals("createdAt cannot be null", exception.getMessage());
    }
    
    @Test
    void shouldBeEqualWhenOrderIdMatches() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        );
        Order order1 = new Order("ORDER-123", "CUST-123", items, BigDecimal.TEN, "CREATED", Instant.now());
        Order order2 = new Order("ORDER-123", "CUST-456", items, BigDecimal.TEN, "COMPLETED", Instant.now());
        
        // When & Then
        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenOrderIdDiffers() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00"))
        );
        Order order1 = new Order("ORDER-123", "CUST-123", items, BigDecimal.TEN, "CREATED", Instant.now());
        Order order2 = new Order("ORDER-456", "CUST-123", items, BigDecimal.TEN, "CREATED", Instant.now());
        
        // When & Then
        assertNotEquals(order1, order2);
    }
}
