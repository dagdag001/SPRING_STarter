package com.ecommerce.shipping.infrastructure.messaging;

import com.ecommerce.shared.messaging.event.PaymentCompletedEvent;
import com.ecommerce.shared.messaging.event.ShipmentCreatedEvent;
import com.ecommerce.shared.messaging.event.StockReservedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ShippingEventListener {

    private final RabbitTemplate rabbitTemplate;
    // In a real app, use a database (Saga State) instead of a map
    private final Map<String, Boolean> paymentStatus = new ConcurrentHashMap<>();
    private final Map<String, Boolean> stockStatus = new ConcurrentHashMap<>();

    public ShippingEventListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "shipping.queue")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("🚚 SHIPPING: Payment confirmed for order {}", event.getOrderId());
        paymentStatus.put(event.getOrderId(), true);
        checkAndShip(event.getOrderId());
    }

    @RabbitListener(queues = "shipping.queue")
    public void handleStockReserved(StockReservedEvent event) {
        log.info("🚚 SHIPPING: Stock reserved for order {}", event.getOrderId());
        stockStatus.put(event.getOrderId(), true);
        checkAndShip(event.getOrderId());
    }

    private void checkAndShip(String orderId) {
        if (Boolean.TRUE.equals(paymentStatus.get(orderId)) && Boolean.TRUE.equals(stockStatus.get(orderId))) {
            log.info("🚀 SHIPPING: Both Payment and Stock ready! Creating shipment for order {}", orderId);
            
            String shipmentId = UUID.randomUUID().toString();
            String trackingNumber = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            ShipmentCreatedEvent event = new ShipmentCreatedEvent(shipmentId, orderId, trackingNumber);
            rabbitTemplate.convertAndSend("app.exchange", "shipment.created", event);
            
            // Clean up state
            paymentStatus.remove(orderId);
            stockStatus.remove(orderId);
        }
    }
}
