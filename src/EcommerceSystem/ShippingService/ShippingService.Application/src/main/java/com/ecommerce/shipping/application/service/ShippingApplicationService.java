package com.ecommerce.shipping.application.service;

import com.ecommerce.shipping.application.usecase.ProcessShipmentUseCase;
import com.ecommerce.shipping.domain.entity.Shipment;
import com.ecommerce.shared.messaging.event.ShipmentCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ShippingApplicationService implements ProcessShipmentUseCase {

    private final RabbitTemplate rabbitTemplate;

    public ShippingApplicationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Shipment initiate(String orderId) {
        String shipmentId = UUID.randomUUID().toString();
        String trackingNumber = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Shipment shipment = Shipment.builder()
                .id(shipmentId)
                .orderId(orderId)
                .trackingNumber(trackingNumber)
                .status(Shipment.ShipmentStatus.SHIPPED)
                .estimatedDelivery(LocalDateTime.now().plusDays(3))
                .build();

        // Publish event
        rabbitTemplate.convertAndSend("app.exchange", "shipment.created", 
                new ShipmentCreatedEvent(shipmentId, orderId, trackingNumber));

        return shipment;
    }
}
