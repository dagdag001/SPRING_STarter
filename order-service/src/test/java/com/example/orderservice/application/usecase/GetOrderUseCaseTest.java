package com.example.orderservice.application.usecase;

import com.example.orderservice.domain.entity.Order;
import com.example.orderservice.domain.repository.OrderRepository;
import com.example.orderservice.domain.valueobject.OrderItem;
import com.example.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GetOrderUseCase.
 */
@ExtendWith(MockitoExtension.class)
class GetOrderUseCaseTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    private GetOrderUseCase getOrderUseCase;
    
    @BeforeEach
    void setUp() {
        getOrderUseCase = new GetOrderUseCase(orderRepository);
    }
    
    @Test
    void shouldRetrieveOrderSuccessfully() {
        // Given
        String orderId = "ORDER-123";
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 2, new BigDecimal("10.00"))
        );
        Order expectedOrder = new Order("CUST-123", items);
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(expectedOrder));
        
        // When
        Order result = getOrderUseCase.execute(orderId);
        
        // Then
        assertNotNull(result);
        assertEquals(expectedOrder, result);
        verify(orderRepository, times(1)).findById(orderId);
    }
    
    @Test
    void shouldThrowNotFoundExceptionWhenOrderDoesNotExist() {
        // Given
        String orderId = "ORDER-999";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        
        // When & Then
        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> getOrderUseCase.execute(orderId)
        );
        assertEquals("Order not found with ID: ORDER-999", exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
    }
    
    @Test
    void shouldThrowExceptionWhenOrderIdIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getOrderUseCase.execute(null)
        );
        assertEquals("Order ID cannot be null or empty", exception.getMessage());
        verify(orderRepository, never()).findById(any());
    }
    
    @Test
    void shouldThrowExceptionWhenOrderIdIsEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getOrderUseCase.execute("")
        );
        assertEquals("Order ID cannot be null or empty", exception.getMessage());
        verify(orderRepository, never()).findById(any());
    }
    
    @Test
    void shouldThrowExceptionWhenOrderIdIsBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getOrderUseCase.execute("   ")
        );
        assertEquals("Order ID cannot be null or empty", exception.getMessage());
        verify(orderRepository, never()).findById(any());
    }
}
