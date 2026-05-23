package com.example.orderservice.application.usecase;

import com.example.orderservice.domain.entity.Order;
import com.example.orderservice.domain.repository.OrderRepository;
import com.example.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case for retrieving order details.
 * 
 * Requirements: 1.2, 5.1
 */
public class GetOrderUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(GetOrderUseCase.class);
    
    private final OrderRepository orderRepository;
    
    public GetOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    /**
     * Retrieves an order by its identifier.
     * 
     * @param orderId The order identifier
     * @return The order
     * @throws NotFoundException if the order is not found
     */
    public Order execute(String orderId) {
        logger.info("Retrieving order with ID: {}", orderId);
        
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.warn("Order not found with ID: {}", orderId);
                    return new NotFoundException("Order not found with ID: " + orderId);
                });
    }
}
