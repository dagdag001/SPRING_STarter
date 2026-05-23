package com.example.shared.messaging;

import com.example.shared.event.Event;

/**
 * Interface for publishing events to the message broker.
 * Implementations should handle serialization and error handling.
 * 
 * Requirements: 3.2, 13.1
 */
public interface EventPublisher {
    
    /**
     * Publishes an event to the message broker with the specified routing key.
     * 
     * @param event The event to publish
     * @param routingKey The routing key for message routing
     * @param <T> The type of the event payload
     * @throws com.example.shared.exception.EventPublishException if publishing fails
     */
    <T> void publish(Event<T> event, String routingKey);
}
