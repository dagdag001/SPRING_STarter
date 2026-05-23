package com.example.orderservice.presentation.controller;

import com.example.orderservice.application.usecase.CreateOrderUseCase;
import com.example.orderservice.application.usecase.GetOrderUseCase;
import com.example.orderservice.domain.entity.Order;
import com.example.orderservice.domain.valueobject.OrderItem;
import com.example.orderservice.presentation.dto.CreateOrderRequest;
import com.example.orderservice.presentation.dto.OrderItemRequest;
import com.example.orderservice.presentation.dto.OrderItemResponse;
import com.example.orderservice.presentation.dto.OrderResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for order operations.
 * 
 * Requirements: 1.4, 14.3, 14.4, 14.5, 14.6
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    
    public OrderController(CreateOrderUseCase createOrderUseCase, GetOrderUseCase getOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }
    
    /**
     * Creates a new order.
     * 
     * @param request The order creation request
     * @return The created order response
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        logger.info("Received create order request for customer: {}", request.getCustomerId());
        
        // Convert presentation DTO to application DTO
        com.example.orderservice.application.dto.CreateOrderRequest appRequest = 
            new com.example.orderservice.application.dto.CreateOrderRequest(
                request.getCustomerId(),
                convertToAppItemRequests(request.getItems())
            );
        
        // Execute use case
        Order order = createOrderUseCase.execute(appRequest);
        
        // Convert domain entity to presentation DTO
        OrderResponse response = toOrderResponse(order);
        
        logger.info("Order created successfully with ID: {}", order.getOrderId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Retrieves an order by its identifier.
     * 
     * @param orderId The order identifier
     * @return The order response
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        logger.info("Received get order request for ID: {}", orderId);
        
        Order order = getOrderUseCase.execute(orderId);
        OrderResponse response = toOrderResponse(order);
        
        return ResponseEntity.ok(response);
    }
    
    private List<com.example.orderservice.application.dto.OrderItemRequest> convertToAppItemRequests(
            List<OrderItemRequest> presentationItems) {
        return presentationItems.stream()
            .map(item -> new com.example.orderservice.application.dto.OrderItemRequest(
                item.getProductId(),
                item.getQuantity(),
                item.getPrice()
            ))
            .collect(Collectors.toList());
    }
    
    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
            .map(item -> new OrderItemResponse(
                item.getProductId(),
                item.getQuantity(),
                item.getPrice()
            ))
            .collect(Collectors.toList());
        
        return new OrderResponse(
            order.getOrderId(),
            order.getCustomerId(),
            itemResponses,
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt()
        );
    }
}
