package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Event published when an order is successfully created.
 * 
 * Requirements: 5.2
 */
public class OrderCreatedEvent extends Event<OrderCreatedPayload> {
    
    /**
     * Constructor for creating a new OrderCreated event.
     * 
     * @param payload The order creation data
     */
    public OrderCreatedEvent(OrderCreatedPayload payload) {
        super("OrderCreated", payload);
    }
    
    /**
     * Constructor for deserialization.
     * 
     * @param eventId The unique identifier for this event
     * @param eventType The type of the event
     * @param timestamp The ISO 8601 timestamp when the event occurred
     * @param payload The order creation data
     */
    @JsonCreator
    public OrderCreatedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("payload") OrderCreatedPayload payload) {
        super(eventId, eventType, timestamp, payload);
    }
}
