package com.example.shippingservice.application.usecase;

import com.example.shippingservice.domain.entity.OrderConfirmation;
import com.example.shippingservice.domain.repository.OrderConfirmationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Use case for correlating order events (payment and stock confirmations).
 * Tracks payment and stock confirmations by order ID.
 * 
 * Requirements: 1.2, 8.1, 8.2, 22.1, 22.2, 22.3
 */
@Service
public class CorrelateOrderEventsUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(CorrelateOrderEventsUseCase.class);
    
    private final OrderConfirmationRepository orderConfirmationRepository;
    
    public CorrelateOrderEventsUseCase(OrderConfirmationRepository orderConfirmationRepository) {
        this.orderConfirmationRepository = orderConfirmationRepository;
    }
    
    /**
     * Records payment confirmation for an order.
     * Creates or updates the OrderConfirmation with paymentConfirmed=true.
     * 
     * @param orderId The order ID
     * @return The updated OrderConfirmation
     */
    public OrderConfirmation recordPaymentConfirmation(String orderId) {
        logger.info("Recording payment confirmation for order: {}", orderId);
        
        Optional<OrderConfirmation> existingConfirmation = orderConfirmationRepository.findByOrderId(orderId);
        
        OrderConfirmation confirmation;
        if (existingConfirmation.isPresent()) {
            // Update existing confirmation
            confirmation = existingConfirmation.get().withPaymentConfirmed();
            logger.debug("Updated existing confirmation for order: {}, paymentConfirmed=true, stockConfirmed={}", 
                orderId, confirmation.isStockConfirmed());
        } else {
            // Create new confirmation
            confirmation = new OrderConfirmation(orderId, true, false, LocalDateTime.now());
            logger.debug("Created new confirmation for order: {}, paymentConfirmed=true, stockConfirmed=false", orderId);
        }
        
        OrderConfirmation saved = orderConfirmationRepository.save(confirmation);
        logger.info("Saved payment confirmation for order: {}, bothConfirmed={}", orderId, saved.isBothConfirmed());
        
        return saved;
    }
    
    /**
     * Records stock confirmation for an order.
     * Creates or updates the OrderConfirmation with stockConfirmed=true.
     * 
     * @param orderId The order ID
     * @return The updated OrderConfirmation
     */
    public OrderConfirmation recordStockConfirmation(String orderId) {
        logger.info("Recording stock confirmation for order: {}", orderId);
        
        Optional<OrderConfirmation> existingConfirmation = orderConfirmationRepository.findByOrderId(orderId);
        
        OrderConfirmation confirmation;
        if (existingConfirmation.isPresent()) {
            // Update existing confirmation
            confirmation = existingConfirmation.get().withStockConfirmed();
            logger.debug("Updated existing confirmation for order: {}, paymentConfirmed={}, stockConfirmed=true", 
                orderId, confirmation.isPaymentConfirmed());
        } else {
            // Create new confirmation
            confirmation = new OrderConfirmation(orderId, false, true, LocalDateTime.now());
            logger.debug("Created new confirmation for order: {}, paymentConfirmed=false, stockConfirmed=true", orderId);
        }
        
        OrderConfirmation saved = orderConfirmationRepository.save(confirmation);
        logger.info("Saved stock confirmation for order: {}, bothConfirmed={}", orderId, saved.isBothConfirmed());
        
        return saved;
    }
    
    /**
     * Checks if both payment and stock confirmations have been received for an order.
     * 
     * @param orderId The order ID
     * @return true if both confirmations are received, false otherwise
     */
    public boolean areBothConfirmationsReceived(String orderId) {
        Optional<OrderConfirmation> confirmation = orderConfirmationRepository.findByOrderId(orderId);
        boolean bothConfirmed = confirmation.map(OrderConfirmation::isBothConfirmed).orElse(false);
        
        logger.debug("Checking confirmations for order: {}, bothConfirmed={}", orderId, bothConfirmed);
        
        return bothConfirmed;
    }
}
