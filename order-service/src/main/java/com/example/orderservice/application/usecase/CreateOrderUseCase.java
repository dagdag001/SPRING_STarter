package com.example.orderservice.application.usecase;

import com.example.orderservice.application.dto.CreateOrderRequest;
import com.example.orderservice.application.dto.OrderItemRequest;
import com.example.orderservice.domain.entity.Order;
import com.example.orderservice.domain.repository.OrderRepository;
import com.example.orderservice.domain.valueobject.OrderItem;
import com.example.shared.event.OrderCreatedEvent;
import com.example.shared.event.OrderCreatedPayload;
import com.example.shared.exception.ValidationException;
import com.example.shared.messaging.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case for creating a new order.
 * Validates input, creates order, and publishes OrderCreated event.
 * 
 * Requirements: 1.2, 5.1, 5.3
 */
public class CreateOrderUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(CreateOrderUseCase.class);
    
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
    
    public CreateOrderUseCase(OrderRepository orderRepository, EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Creates a new order.
     * 
     * @param request The order creation request
     * @return The created order
     * @throws ValidationException if validation fails
     */
    public Order execute(CreateOrderRequest request) {
        logger.info("Creating order for customer: {}", request.getCustomerId());
        
        // Validate input
        validateRequest(request);
        
        // Convert DTOs to domain objects
        List<OrderItem> orderItems = convertToOrderItems(request.getItems());
        
        // Create order
        Order order = new Order(request.getCustomerId(), orderItems);
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        logger.info("Order created successfully with ID: {}", savedOrder.getOrderId());
        
        // Publish OrderCreated event
        publishOrderCreatedEvent(savedOrder);
        
        return savedOrder;
    }
    
    private void validateRequest(CreateOrderRequest request) {
        if (request == null) {
            throw new ValidationException("Order request cannot be null");
        }
        
        if (request.getCustomerId() == null || request.getCustomerId().trim().isEmpty()) {
            throw new ValidationException("Customer ID cannot be empty");
        }
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ValidationException("Items list cannot be empty");
        }
        
        // Validate each item
        for (OrderItemRequest item : request.getItems()) {
            if (item.getProductId() == null || item.getProductId().trim().isEmpty()) {
                throw new ValidationException("Product ID cannot be empty");
            }
            
            if (item.getQuantity() == null || item.getQuantity() < 1) {
                throw new ValidationException("Quantity must be at least 1");
            }
            
            if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Price cannot be negative");
            }
        }
    }
    
    private List<OrderItem> convertToOrderItems(List<OrderItemRequest> itemRequests) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : itemRequests) {
            orderItems.add(new OrderItem(
                itemRequest.getProductId(),
                itemRequest.getQuantity(),
                itemRequest.getPrice()
            ));
        }
        return orderItems;
    }
    
    private void publishOrderCreatedEvent(Order order) {
        // Convert domain OrderItems to event OrderItems
        List<com.example.shared.event.OrderItem> eventItems = order.getItems().stream()
            .map(item -> new com.example.shared.event.OrderItem(
                item.getProductId(),
                item.getQuantity(),
                item.getPrice()
            ))
            .collect(Collectors.toList());
        
        OrderCreatedPayload payload = new OrderCreatedPayload(
            order.getOrderId(),
            order.getCustomerId(),
            eventItems,
            order.getTotalAmount()
        );
        
        OrderCreatedEvent event = new OrderCreatedEvent(payload);
        
        eventPublisher.publish(event, "order.created");
        logger.info("Published OrderCreated event for order: {}", order.getOrderId());
    }
}
