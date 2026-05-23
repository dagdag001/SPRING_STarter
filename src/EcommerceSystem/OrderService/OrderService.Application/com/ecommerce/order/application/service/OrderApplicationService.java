package com.ecommerce.order.application.service;

import com.ecommerce.order.application.ports.EventPublisher;
import com.ecommerce.shared.messaging.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final EventPublisher eventPublisher;

    public String createOrder(String userId, BigDecimal amount) {
        String orderId = UUID.randomUUID().toString();
        
        // In a real app: Save to DB via repository
        
        // Publish event to trigger Payment and Inventory
        eventPublisher.publish(new OrderCreatedEvent(orderId, userId, amount));
        
        return orderId;
    }
}
