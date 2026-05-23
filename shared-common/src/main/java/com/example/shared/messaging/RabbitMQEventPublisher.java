package com.example.shared.messaging;

import com.example.shared.config.RabbitMQConfig;
import com.example.shared.event.Event;
import com.example.shared.exception.EventPublishException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ implementation of EventPublisher.
 * Publishes events to RabbitMQ using RabbitTemplate with JSON serialization.
 * 
 * Requirements: 3.2, 13.1
 */
@Component
public class RabbitMQEventPublisher implements EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQEventPublisher.class);
    
    private final RabbitTemplate rabbitTemplate;
    
    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    /**
     * Publishes an event to RabbitMQ with the specified routing key.
     * Logs the event publication and handles any errors.
     * 
     * @param event The event to publish
     * @param routingKey The routing key for message routing
     * @param <T> The type of the event payload
     * @throws EventPublishException if publishing fails
     */
    @Override
    public <T> void publish(Event<T> event, String routingKey) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (routingKey == null || routingKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Routing key cannot be null or empty");
        }
        
        try {
            logger.info("Publishing event: eventId={}, eventType={}, routingKey={}", 
                       event.getEventId(), event.getEventType(), routingKey);
            
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, event);
            
            logger.debug("Successfully published event: eventId={}, eventType={}", 
                        event.getEventId(), event.getEventType());
            
        } catch (Exception e) {
            logger.error("Failed to publish event: eventId={}, eventType={}, routingKey={}, error={}", 
                        event.getEventId(), event.getEventType(), routingKey, e.getMessage(), e);
            throw new EventPublishException(event.getEventType(), e);
        }
    }
}
