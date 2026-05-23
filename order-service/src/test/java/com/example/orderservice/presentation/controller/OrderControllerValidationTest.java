package com.example.orderservice.presentation.controller;

import com.example.orderservice.presentation.dto.CreateOrderRequest;
import com.example.orderservice.presentation.dto.OrderItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for REST API validation in OrderController.
 * Uses @WithMockUser to bypass JWT authentication for validation testing.
 * 
 * Tests Requirements 14.5 and 14.6:
 * - REST endpoints validate request payload
 * - REST endpoints return HTTP 400 with error details for invalid data
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerValidationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @WithMockUser
    void createOrderWithBlankCustomerId_shouldReturnBadRequest() throws Exception {
        List<OrderItemRequest> items = List.of(
            new OrderItemRequest("prod-001", 2, BigDecimal.valueOf(29.99))
        );
        CreateOrderRequest request = new CreateOrderRequest("", items);
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/orders"));
    }
    
    @Test
    @WithMockUser
    void createOrderWithEmptyItems_shouldReturnBadRequest() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest("customer-123", new ArrayList<>());
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details.items").exists());
    }
    
    @Test
    @WithMockUser
    void createOrderWithNullItems_shouldReturnBadRequest() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest("customer-123", null);
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"));
    }
    
    @Test
    @WithMockUser
    void createOrderWithBlankProductId_shouldReturnBadRequest() throws Exception {
        List<OrderItemRequest> items = List.of(
            new OrderItemRequest("", 2, BigDecimal.valueOf(29.99))
        );
        CreateOrderRequest request = new CreateOrderRequest("customer-123", items);
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details['items[0].productId']").exists());
    }
    
    @Test
    @WithMockUser
    void createOrderWithZeroQuantity_shouldReturnBadRequest() throws Exception {
        List<OrderItemRequest> items = List.of(
            new OrderItemRequest("prod-001", 0, BigDecimal.valueOf(29.99))
        );
        CreateOrderRequest request = new CreateOrderRequest("customer-123", items);
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details['items[0].quantity']").exists());
    }
    
    @Test
    @WithMockUser
    void createOrderWithNegativeQuantity_shouldReturnBadRequest() throws Exception {
        List<OrderItemRequest> items = List.of(
            new OrderItemRequest("prod-001", -5, BigDecimal.valueOf(29.99))
        );
        CreateOrderRequest request = new CreateOrderRequest("customer-123", items);
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details['items[0].quantity']").exists());
    }
    
    @Test
    @WithMockUser
    void createOrderWithNegativePrice_shouldReturnBadRequest() throws Exception {
        List<OrderItemRequest> items = List.of(
            new OrderItemRequest("prod-001", 2, BigDecimal.valueOf(-10.00))
        );
        CreateOrderRequest request = new CreateOrderRequest("customer-123", items);
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details['items[0].price']").exists());
    }
    
    @Test
    @WithMockUser
    void createOrderWithNullQuantity_shouldReturnBadRequest() throws Exception {
        List<OrderItemRequest> items = List.of(
            new OrderItemRequest("prod-001", null, BigDecimal.valueOf(29.99))
        );
        CreateOrderRequest request = new CreateOrderRequest("customer-123", items);
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details['items[0].quantity']").exists());
    }
    
    @Test
    @WithMockUser
    void createOrderWithNullPrice_shouldReturnBadRequest() throws Exception {
        List<OrderItemRequest> items = List.of(
            new OrderItemRequest("prod-001", 2, null)
        );
        CreateOrderRequest request = new CreateOrderRequest("customer-123", items);
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details['items[0].price']").exists());
    }
}
