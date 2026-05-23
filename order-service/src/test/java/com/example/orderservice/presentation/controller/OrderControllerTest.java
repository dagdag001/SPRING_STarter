package com.example.orderservice.presentation.controller;

import com.example.orderservice.application.usecase.CreateOrderUseCase;
import com.example.orderservice.application.usecase.GetOrderUseCase;
import com.example.orderservice.domain.entity.Order;
import com.example.orderservice.domain.valueobject.OrderItem;
import com.example.orderservice.presentation.dto.CreateOrderRequest;
import com.example.orderservice.presentation.dto.OrderItemRequest;
import com.example.orderservice.presentation.dto.OrderResponse;
import com.example.shared.exception.NotFoundException;
import com.example.shared.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for OrderController.
 * Uses @WithMockUser to bypass JWT authentication for unit testing.
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CreateOrderUseCase createOrderUseCase;
    
    @MockBean
    private GetOrderUseCase getOrderUseCase;
    
    @Test
    @WithMockUser
    void shouldCreateOrderSuccessfully() throws Exception {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 2, new BigDecimal("10.00")),
            new OrderItemRequest("PROD-2", 1, new BigDecimal("15.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        List<OrderItem> domainItems = Arrays.asList(
            new OrderItem("PROD-1", 2, new BigDecimal("10.00")),
            new OrderItem("PROD-2", 1, new BigDecimal("15.00"))
        );
        Order order = new Order("CUST-123", domainItems);
        
        when(createOrderUseCase.execute(any())).thenReturn(order);
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", notNullValue()))
                .andExpect(jsonPath("$.customerId", is("CUST-123")))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.totalAmount", is(35.00)))
                .andExpect(jsonPath("$.status", is("CREATED")));
        
        verify(createOrderUseCase, times(1)).execute(any());
    }
    
    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenCustomerIdIsNull() throws Exception {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 1, new BigDecimal("10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest(null, items);
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(createOrderUseCase, never()).execute(any());
    }
    
    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenItemsListIsEmpty() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", Arrays.asList());
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(createOrderUseCase, never()).execute(any());
    }
    
    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 1, new BigDecimal("10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        when(createOrderUseCase.execute(any())).thenThrow(new ValidationException("Validation failed"));
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(createOrderUseCase, times(1)).execute(any());
    }
    
    @Test
    @WithMockUser
    void shouldGetOrderSuccessfully() throws Exception {
        // Given
        String orderId = "ORDER-123";
        List<OrderItem> items = Arrays.asList(
            new OrderItem("PROD-1", 2, new BigDecimal("10.00"))
        );
        Order order = new Order("CUST-123", items);
        
        when(getOrderUseCase.execute(orderId)).thenReturn(order);
        
        // When & Then
        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", notNullValue()))
                .andExpect(jsonPath("$.customerId", is("CUST-123")))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId", is("PROD-1")))
                .andExpect(jsonPath("$.items[0].quantity", is(2)))
                .andExpect(jsonPath("$.items[0].price", is(10.00)))
                .andExpect(jsonPath("$.totalAmount", is(20.00)))
                .andExpect(jsonPath("$.status", is("CREATED")));
        
        verify(getOrderUseCase, times(1)).execute(orderId);
    }
    
    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        // Given
        String orderId = "ORDER-999";
        when(getOrderUseCase.execute(orderId)).thenThrow(new NotFoundException("Order not found"));
        
        // When & Then
        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isNotFound());
        
        verify(getOrderUseCase, times(1)).execute(orderId);
    }
    
    @Test
    @WithMockUser
    void shouldHandleMultipleItemsInOrder() throws Exception {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 1, new BigDecimal("10.00")),
            new OrderItemRequest("PROD-2", 2, new BigDecimal("15.00")),
            new OrderItemRequest("PROD-3", 3, new BigDecimal("20.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        List<OrderItem> domainItems = Arrays.asList(
            new OrderItem("PROD-1", 1, new BigDecimal("10.00")),
            new OrderItem("PROD-2", 2, new BigDecimal("15.00")),
            new OrderItem("PROD-3", 3, new BigDecimal("20.00"))
        );
        Order order = new Order("CUST-123", domainItems);
        
        when(createOrderUseCase.execute(any())).thenReturn(order);
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.totalAmount", is(100.00)));
        
        verify(createOrderUseCase, times(1)).execute(any());
    }
    
    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenItemQuantityIsZero() throws Exception {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 0, new BigDecimal("10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(createOrderUseCase, never()).execute(any());
    }
    
    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenItemPriceIsNegative() throws Exception {
        // Given
        List<OrderItemRequest> items = Arrays.asList(
            new OrderItemRequest("PROD-1", 1, new BigDecimal("-10.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest("CUST-123", items);
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        verify(createOrderUseCase, never()).execute(any());
    }
}
