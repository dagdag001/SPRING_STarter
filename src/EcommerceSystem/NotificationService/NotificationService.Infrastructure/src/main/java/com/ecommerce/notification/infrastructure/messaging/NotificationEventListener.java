package com.ecommerce.notification.infrastructure.messaging;

import com.ecommerce.notification.api.controller.NotificationController;
import com.ecommerce.shared.messaging.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class NotificationEventListener {

    @RabbitListener(queues = "notification.queue")
    public void handleUserRegistered(UserRegisteredEvent event) {
        String msg = "🔔 [" + LocalDateTime.now() + "] User registered: " + event.getFullName();
        log.info(msg);
        NotificationController.NOTIFICATION_LOGS.add(msg);
    }

    @RabbitListener(queues = "notification.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        String msg = "🔔 [" + LocalDateTime.now() + "] Order created: " + event.getOrderId();
        log.info(msg);
        NotificationController.NOTIFICATION_LOGS.add(msg);
    }

    @RabbitListener(queues = "notification.queue")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("🔔 NOTIFICATION: Payment {} confirmed for Order {}", event.getPaymentId(), event.getOrderId());
    }

    @RabbitListener(queues = "notification.queue")
    public void handleStockReserved(StockReservedEvent event) {
        log.info("🔔 NOTIFICATION: Inventory secured for Order {}", event.getOrderId());
    }

    @RabbitListener(queues = "notification.queue")
    public void handleShipmentCreated(ShipmentCreatedEvent event) {
        log.info("🔔 NOTIFICATION: Package shipped! Tracking: {}", event.getTrackingNumber());
    }
}
