package com.ecommerce.payment.infrastructure.messaging;

import com.ecommerce.payment.application.ports.PaymentEventPublisher;
import com.ecommerce.shared.messaging.event.OrderCreatedEvent;
import com.ecommerce.shared.messaging.event.PaymentCompletedEvent;
import com.ecommerce.shared.messaging.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedListener {

    private final PaymentEventPublisher eventPublisher;

    @RabbitListener(queues = "payment.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("💳 PAYMENT: Received OrderCreatedEvent for order {}", event.getOrderId());
        
        // Mock payment logic
        boolean success = event.getTotalAmount().doubleValue() < 1000.0; // Fail for expensive orders
        
        if (success) {
            log.info("✅ PAYMENT: Payment successful for order {}", event.getOrderId());
            eventPublisher.publish(new PaymentCompletedEvent(UUID.randomUUID().toString(), event.getOrderId()));
        } else {
            log.warn("❌ PAYMENT: Payment failed for order {}", event.getOrderId());
            eventPublisher.publish(new PaymentFailedEvent(event.getOrderId(), "Insufficient funds or limit exceeded"));
        }
    }
}
