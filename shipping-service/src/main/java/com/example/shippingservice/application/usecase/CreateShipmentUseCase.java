package com.example.shippingservice.application.usecase;

import com.example.shared.event.ShipmentCreatedEvent;
import com.example.shared.event.ShipmentCreatedPayload;
import com.example.shared.messaging.EventPublisher;
import com.example.shippingservice.domain.entity.Shipment;
import com.example.shippingservice.domain.repository.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Use case for creating shipments.
 * Creates shipments when both payment and stock confirmations are received.
 * 
 * Requirements: 1.2, 8.1, 8.2, 22.1, 22.2, 22.3
 */
@Service
public class CreateShipmentUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(CreateShipmentUseCase.class);
    
    private final ShipmentRepository shipmentRepository;
    private final EventPublisher eventPublisher;
    
    @Value("${rabbitmq.routing-key.shipment-created:shipment.created}")
    private String shipmentCreatedRoutingKey;
    
    @Value("${shipment.delivery-days:5}")
    private int deliveryDays;
    
    public CreateShipmentUseCase(ShipmentRepository shipmentRepository, EventPublisher eventPublisher) {
        this.shipmentRepository = shipmentRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Creates a shipment for an order.
     * Should only be called when both payment and stock confirmations are received.
     * 
     * @param orderId The order ID
     */
    public void createShipment(String orderId) {
        logger.info("Creating shipment for order: {}", orderId);
        
        // Check if shipment already exists for this order
        if (shipmentRepository.existsByOrderId(orderId)) {
            logger.warn("Shipment already exists for order: {}", orderId);
            return;
        }
        
        String shipmentId = UUID.randomUUID().toString();
        LocalDate estimatedDeliveryDate = LocalDate.now().plusDays(deliveryDays);
        LocalDateTime createdAt = LocalDateTime.now();
        
        // Create shipment
        Shipment shipment = new Shipment(shipmentId, orderId, estimatedDeliveryDate, "CREATED", createdAt);
        shipmentRepository.save(shipment);
        
        logger.info("Shipment created successfully: shipmentId={}, orderId={}, estimatedDeliveryDate={}", 
            shipmentId, orderId, estimatedDeliveryDate);
        
        // Publish ShipmentCreated event
        ShipmentCreatedPayload payload = new ShipmentCreatedPayload(shipmentId, orderId, estimatedDeliveryDate.toString());
        ShipmentCreatedEvent event = new ShipmentCreatedEvent(payload);
        eventPublisher.publish(event, shipmentCreatedRoutingKey);
        
        logger.info("Published ShipmentCreated event for order: {}", orderId);
    }
}
