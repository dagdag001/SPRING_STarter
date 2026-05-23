package com.example.orderservice.infrastructure.persistence;

import com.example.orderservice.domain.entity.Order;
import com.example.orderservice.domain.valueobject.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderRepositoryImpl.
 */
@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {
    
    @Mock
    private OrderJpaRepository jpaRepository;
    
    private OrderRepositoryImpl orderRepository;
    
    @BeforeEach
    void setUp() {
        orderRepository = new OrderRepositoryImpl(jpaRepository);
    }
    
    @Test
    void shouldSaveOrderSuccessfully() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 2, new BigDecimal("10.00")),
            new OrderItem("PROD-2", 1, new BigDecimal("15.00"))
        );
        Order order = new Order("CUST-123", items);
        
        when(jpaRepository.save(any(OrderJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Order savedOrder = orderRepository.save(order);
        
        // Then
        assertNotNull(savedOrder);
        assertEquals(order.getOrderId(), savedOrder.getOrderId());
        assertEquals(order.getCustomerId(), savedOrder.getCustomerId());
        assertEquals(order.getItems().size(), savedOrder.getItems().size());
        assertEquals(order.getTotalAmount(), savedOrder.getTotalAmount());
        assertEquals(order.getStatus(), savedOrder.getStatus());
        
        ArgumentCaptor<OrderJpaEntity> entityCaptor = ArgumentCaptor.forClass(OrderJpaEntity.class);
        verify(jpaRepository, times(1)).save(entityCaptor.capture());
        
        OrderJpaEntity capturedEntity = entityCaptor.getValue();
        assertEquals(order.getOrderId(), capturedEntity.getOrderId());
        assertEquals(order.getCustomerId(), capturedEntity.getCustomerId());
        assertEquals(2, capturedEntity.getItems().size());
    }
    
    @Test
    void shouldFindOrderByIdSuccessfully() {
        // Given
        String orderId = "ORDER-123";
        OrderJpaEntity jpaEntity = new OrderJpaEntity(
            orderId,
            "CUST-123",
            new BigDecimal("35.00"),
            "CREATED",
            Instant.now()
        );
        
        OrderItemJpaEntity item1 = new OrderItemJpaEntity("PROD-1", 2, new BigDecimal("10.00"));
        OrderItemJpaEntity item2 = new OrderItemJpaEntity("PROD-2", 1, new BigDecimal("15.00"));
        jpaEntity.addItem(item1);
        jpaEntity.addItem(item2);
        
        when(jpaRepository.findById(orderId)).thenReturn(Optional.of(jpaEntity));
        
        // When
        Optional<Order> result = orderRepository.findById(orderId);
        
        // Then
        assertTrue(result.isPresent());
        Order order = result.get();
        assertEquals(orderId, order.getOrderId());
        assertEquals("CUST-123", order.getCustomerId());
        assertEquals(2, order.getItems().size());
        assertEquals(new BigDecimal("35.00"), order.getTotalAmount());
        assertEquals("CREATED", order.getStatus());
        
        verify(jpaRepository, times(1)).findById(orderId);
    }
    
    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        // Given
        String orderId = "ORDER-999";
        when(jpaRepository.findById(orderId)).thenReturn(Optional.empty());
        
        // When
        Optional<Order> result = orderRepository.findById(orderId);
        
        // Then
        assertFalse(result.isPresent());
        verify(jpaRepository, times(1)).findById(orderId);
    }
    
    @Test
    void shouldCheckIfOrderExistsById() {
        // Given
        String orderId = "ORDER-123";
        when(jpaRepository.existsById(orderId)).thenReturn(true);
        
        // When
        boolean exists = orderRepository.existsById(orderId);
        
        // Then
        assertTrue(exists);
        verify(jpaRepository, times(1)).existsById(orderId);
    }
    
    @Test
    void shouldReturnFalseWhenOrderDoesNotExist() {
        // Given
        String orderId = "ORDER-999";
        when(jpaRepository.existsById(orderId)).thenReturn(false);
        
        // When
        boolean exists = orderRepository.existsById(orderId);
        
        // Then
        assertFalse(exists);
        verify(jpaRepository, times(1)).existsById(orderId);
    }
    
    @Test
    void shouldConvertDomainEntityToJpaEntityCorrectly() {
        // Given
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 3, new BigDecimal("25.00"))
        );
        Order order = new Order("ORDER-123", "CUST-456", items, new BigDecimal("75.00"), "COMPLETED", Instant.now());
        
        when(jpaRepository.save(any(OrderJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        orderRepository.save(order);
        
        // Then
        ArgumentCaptor<OrderJpaEntity> entityCaptor = ArgumentCaptor.forClass(OrderJpaEntity.class);
        verify(jpaRepository, times(1)).save(entityCaptor.capture());
        
        OrderJpaEntity capturedEntity = entityCaptor.getValue();
        assertEquals("ORDER-123", capturedEntity.getOrderId());
        assertEquals("CUST-456", capturedEntity.getCustomerId());
        assertEquals(new BigDecimal("75.00"), capturedEntity.getTotalAmount());
        assertEquals("COMPLETED", capturedEntity.getStatus());
        assertEquals(1, capturedEntity.getItems().size());
        
        OrderItemJpaEntity capturedItem = capturedEntity.getItems().get(0);
        assertEquals("PROD-1", capturedItem.getProductId());
        assertEquals(3, capturedItem.getQuantity());
        assertEquals(new BigDecimal("25.00"), capturedItem.getPrice());
    }
    
    @Test
    void shouldConvertJpaEntityToDomainEntityCorrectly() {
        // Given
        String orderId = "ORDER-123";
        Instant createdAt = Instant.now();
        OrderJpaEntity jpaEntity = new OrderJpaEntity(
            orderId,
            "CUST-789",
            new BigDecimal("100.00"),
            "SHIPPED",
            createdAt
        );
        
        OrderItemJpaEntity item = new OrderItemJpaEntity("PROD-5", 4, new BigDecimal("25.00"));
        jpaEntity.addItem(item);
        
        when(jpaRepository.findById(orderId)).thenReturn(Optional.of(jpaEntity));
        
        // When
        Optional<Order> result = orderRepository.findById(orderId);
        
        // Then
        assertTrue(result.isPresent());
        Order order = result.get();
        assertEquals("ORDER-123", order.getOrderId());
        assertEquals("CUST-789", order.getCustomerId());
        assertEquals(new BigDecimal("100.00"), order.getTotalAmount());
        assertEquals("SHIPPED", order.getStatus());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(1, order.getItems().size());
        
        OrderItem orderItem = order.getItems().get(0);
        assertEquals("PROD-5", orderItem.getProductId());
        assertEquals(4, orderItem.getQuantity());
        assertEquals(new BigDecimal("25.00"), orderItem.getPrice());
    }
}
