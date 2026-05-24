package com.example.shared.messaging;

import com.example.shared.config.RabbitMQConfig;
import com.example.shared.event.Event;
import com.example.shared.event.OrderCreatedEvent;
import com.example.shared.event.OrderCreatedPayload;
import com.example.shared.event.OrderItem;
import com.example.shared.exception.EventPublishException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RabbitMQEventPublisher.
 * Tests event publishing logic, error handling, and validation.
 */
@ExtendWith(MockitoExtension.class)
class RabbitMQEventPublisherTest {
    
    @Mock
    private RabbitTemplate rabbitTemplate;
    
    private RabbitMQEventPublisher eventPublisher;
    
    @BeforeEach
    void setUp() {
        eventPublisher = new RabbitMQEventPublisher(rabbitTemplate);
    }
    
    @Test
    void publish_WithValidEvent_ShouldPublishToRabbitMQ() {
        // Arrange
        OrderItem item = new OrderItem("prod-001", 2, new BigDecimal("29.99"));
        OrderCreatedPayload payload = new OrderCreatedPayload(
            "order-123",
            "customer-456",
            List.of(item),
            new BigDecimal("59.98")
        );
        OrderCreatedEvent event = new OrderCreatedEvent(payload);
        String routingKey = "order.created";
        
        // Act
        eventPublisher.publish(event, routingKey);
        
        // Assert
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(rabbitTemplate, times(1)).convertAndSend(
            eq(RabbitMQConfig.EXCHANGE_NAME),
            eq(routingKey),
            eventCaptor.capture()
        );
        
        Event capturedEvent = eventCaptor.getValue();
        assertEquals(event.getEventId(), capturedEvent.getEventId());
        assertEquals(event.getEventType(), capturedEvent.getEventType());
    }
    
    @Test
    void publish_WithNullEvent_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            eventPublisher.publish(null, "order.created");
        });
        
        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(String.class), any(Event.class));
    }
    
    @Test
    void publish_WithNullRoutingKey_ShouldThrowIllegalArgumentException() {
        // Arrange
        OrderItem item = new OrderItem("prod-001", 2, new BigDecimal("29.99"));
        OrderCreatedPayload payload = new OrderCreatedPayload(
            "order-123",
            "customer-456",
            List.of(item),
            new BigDecimal("59.98")
        );
        OrderCreatedEvent event = new OrderCreatedEvent(payload);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            eventPublisher.publish(event, null);
        });
        
        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(String.class), any(Event.class));
    }
    
    @Test
    void publish_WithEmptyRoutingKey_ShouldThrowIllegalArgumentException() {
        // Arrange
        OrderItem item = new OrderItem("prod-001", 2, new BigDecimal("29.99"));
        OrderCreatedPayload payload = new OrderCreatedPayload(
            "order-123",
            "customer-456",
            List.of(item),
            new BigDecimal("59.98")
        );
        OrderCreatedEvent event = new OrderCreatedEvent(payload);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            eventPublisher.publish(event, "");
        });
        
        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(String.class), any(Event.class));
    }
    
    @Test
    void publish_WhenRabbitTemplateThrowsException_ShouldThrowEventPublishException() {
        // Arrange
        OrderItem item = new OrderItem("prod-001", 2, new BigDecimal("29.99"));
        OrderCreatedPayload payload = new OrderCreatedPayload(
            "order-123",
            "customer-456",
            List.of(item),
            new BigDecimal("59.98")
        );
        OrderCreatedEvent event = new OrderCreatedEvent(payload);
        String routingKey = "order.created";
        
        doThrow(new RuntimeException("RabbitMQ connection failed"))
            .when(rabbitTemplate)
            .convertAndSend(any(String.class), any(String.class), any(Event.class));
        
        // Act & Assert
        EventPublishException exception = assertThrows(EventPublishException.class, () -> {
            eventPublisher.publish(event, routingKey);
        });
        
        assertTrue(exception.getMessage().contains("OrderCreated"));
        assertNotNull(exception.getCause());
    }
}
