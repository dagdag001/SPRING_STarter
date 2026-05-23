package com.example.orderservice.application.usecase;

import com.example.orderservice.application.dto.CreateOrderRequest;
import com.example.orderservice.application.dto.OrderItemRequest;
import com.example.orderservice.domain.entity.Order;
import com.example.orderservice.domain.repository.OrderRepository;
import com.example.shared.exception.ValidationException;
import com.example.shared.messaging.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateOrderUseCase.
 */
@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    private CreateOrderUseCase createOrderUseCase;
    
    @BeforeEach
    void setUp() {
        createOrderUseCase = new CreateOrderUseCase(orderRepository, eventPublisher);
    }
    
    @Test
    void shouldCreateOrderSuccessfully() {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 2, new BigDecimal("10.00")),
            new OrderItemRequest("PROD-2", 1, new BigDecimal("15.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Order result = createOrderUseCase.execute(request);
        
        // Then
        assertNotNull(result);
        assertEquals("CUST-123", result.getCustomerId());
        assertEquals(2, result.getItems().size());
        assertEquals(new BigDecimal("35.00"), result.getTotalAmount());
        assertEquals("CREATED", result.getStatus());
        
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(eventPublisher, times(1)).publish(any(), eq("order.created"));
    }
    
    @Test
    void shouldPublishOrderCreatedEvent() {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 1, new BigDecimal("10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        createOrderUseCase.execute(request);
        
        // Then
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventPublisher, times(1)).publish(any(), routingKeyCaptor.capture());
        assertEquals("order.created", routingKeyCaptor.getValue());
    }
    
    @Test
    void shouldThrowExceptionWhenRequestIsNull() {
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> createOrderUseCase.execute(null)
        );
        assertEquals("Order request cannot be null", exception.getMessage());
        
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerIdIsNull() {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 1, new BigDecimal("10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest(null, items);
        
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Customer ID cannot be empty", exception.getMessage());
        
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerIdIsEmpty() {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 1, new BigDecimal("10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("", items);
        
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Customer ID cannot be empty", exception.getMessage());
        
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenItemsListIsNull() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", null);
        
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Items list cannot be empty", exception.getMessage());
        
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenItemsListIsEmpty() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", new ArrayList<>());
        
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Items list cannot be empty", exception.getMessage());
        
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenItemProductIdIsNull() {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest(null, 1, new BigDecimal("10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Product ID cannot be empty", exception.getMessage());
        
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenItemProductIdIsEmpty() {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("", 1, new BigDecimal("10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Product ID cannot be empty", exception.getMessage());
        
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenItemQuantityIsNull() {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", null, new BigDecimal("10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Quantity must be at least 1", exception.getMessage());
        
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenItemQuantityIsZero() {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 0, new BigDecimal("10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Quantity must be at least 1", exception.getMessage());
        
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenItemQuantityIsNegative() {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", -1, new BigDecimal("10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Quantity must be at least 1", exception.getMessage());
        
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenItemPriceIsNull() {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 1, null)
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Price cannot be negative", exception.getMessage());
        
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
    
    @Test
    void shouldThrowExceptionWhenItemPriceIsNegative() {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 1, new BigDecimal("-10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Price cannot be negative", exception.getMessage());
        
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any());
    }
    
    @Test
    void shouldAcceptZeroPrice() {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 1, BigDecimal.ZERO)
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Order result = createOrderUseCase.execute(request);
        
        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(eventPublisher, times(1)).publish(any(), eq("order.created"));
    }
}
