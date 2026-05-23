package com.example.inventoryservice.application.usecase;

import com.example.inventoryservice.domain.entity.Product;
import com.example.inventoryservice.domain.entity.StockReservation;
import com.example.inventoryservice.domain.repository.ProductRepository;
import com.example.inventoryservice.domain.repository.StockReservationRepository;
import com.example.shared.event.StockFailedEvent;
import com.example.shared.event.StockFailedPayload;
import com.example.shared.event.StockReservedEvent;
import com.example.shared.event.StockReservedPayload;
import com.example.shared.messaging.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Use case for reserving stock for orders.
 * Implements stock checking and reservation logic with event publishing.
 * 
 * Requirements: 1.2, 7.1, 7.2, 7.3
 */
@Service
public class ReserveStockUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(ReserveStockUseCase.class);
    
    private final ProductRepository productRepository;
    private final StockReservationRepository stockReservationRepository;
    private final CheckStockUseCase checkStockUseCase;
    private final EventPublisher eventPublisher;
    
    @Value("${rabbitmq.routing-key.stock-reserved:stock.reserved}")
    private String stockReservedRoutingKey;
    
    @Value("${rabbitmq.routing-key.stock-failed:stock.failed}")
    private String stockFailedRoutingKey;
    
    public ReserveStockUseCase(ProductRepository productRepository,
                               StockReservationRepository stockReservationRepository,
                               CheckStockUseCase checkStockUseCase,
                               EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.stockReservationRepository = stockReservationRepository;
        this.checkStockUseCase = checkStockUseCase;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Reserves stock for an order.
     * Checks stock availability, reserves stock if available, and publishes appropriate event.
     * 
     * @param orderId The order ID
     * @param items Map of productId to requested quantity
     */
    @Transactional
    public void reserveStock(String orderId, Map<String, Integer> items) {
        logger.info("Reserving stock for order: {}, items: {}", orderId, items);
        
        // Check if reservation already exists for this order
        if (stockReservationRepository.existsByOrderId(orderId)) {
            logger.warn("Stock reservation already exists for order: {}", orderId);
            return;
        }
        
        // Check stock availability for all items
        Map<String, Boolean> availability = checkStockUseCase.checkStockAvailability(items);
        
        // Find unavailable products
        List<String> unavailableProducts = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : availability.entrySet()) {
            if (!entry.getValue()) {
                unavailableProducts.add(entry.getKey());
            }
        }
        
        if (!unavailableProducts.isEmpty()) {
            // Stock unavailable - publish StockFailed event
            logger.warn("Insufficient stock for order: {}, unavailable products: {}", orderId, unavailableProducts);
            
            StockFailedPayload payload = new StockFailedPayload(orderId, unavailableProducts);
            StockFailedEvent event = new StockFailedEvent(payload);
            eventPublisher.publish(event, stockFailedRoutingKey);
            
            logger.info("Published StockFailed event for order: {}", orderId);
            return;
        }
        
        // All items available - reserve stock
        List<StockReservedPayload.Reservation> reservations = new ArrayList<>();
        LocalDateTime reservedAt = LocalDateTime.now();
        
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            
            // Get product and reserve stock
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalStateException("Product not found: " + productId));
            
            product.reserveStock(quantity);
            productRepository.save(product);
            
            // Create stock reservation record
            String reservationId = UUID.randomUUID().toString();
            StockReservation reservation = new StockReservation(
                    reservationId, orderId, productId, quantity, reservedAt
            );
            stockReservationRepository.save(reservation);
            
            // Add to reservations list for event
            reservations.add(new StockReservedPayload.Reservation(productId, quantity));
            
            logger.debug("Reserved {} units of product {} for order {}", quantity, productId, orderId);
        }
        
        // Publish StockReserved event
        StockReservedPayload payload = new StockReservedPayload(orderId, reservations);
        StockReservedEvent event = new StockReservedEvent(payload);
        eventPublisher.publish(event, stockReservedRoutingKey);
        
        logger.info("Successfully reserved stock for order: {}, published StockReserved event", orderId);
    }
}
