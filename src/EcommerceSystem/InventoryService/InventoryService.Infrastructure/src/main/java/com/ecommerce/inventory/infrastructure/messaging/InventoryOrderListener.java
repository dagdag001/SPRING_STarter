package com.ecommerce.inventory.infrastructure.messaging;

import com.ecommerce.inventory.application.ports.InventoryEventPublisher;
import com.ecommerce.shared.messaging.event.OrderCreatedEvent;
import com.ecommerce.shared.messaging.event.StockFailedEvent;
import com.ecommerce.shared.messaging.event.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryOrderListener {

    private final InventoryEventPublisher eventPublisher;

    @RabbitListener(queues = "inventory.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("📊 INVENTORY: Checking stock for order {}", event.getOrderId());
        
        // Mock stock logic
        boolean inStock = true; // Assume always in stock for this demo
        
        if (inStock) {
            log.info("✅ INVENTORY: Stock reserved for order {}", event.getOrderId());
            eventPublisher.publish(new StockReservedEvent(event.getOrderId()));
        } else {
            log.warn("❌ INVENTORY: Stock unavailable for order {}", event.getOrderId());
            eventPublisher.publish(new StockFailedEvent(event.getOrderId(), "Product out of stock"));
        }
    }
}
