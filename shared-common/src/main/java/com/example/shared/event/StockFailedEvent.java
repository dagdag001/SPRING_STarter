package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Event published when stock reservation fails for an order.
 * 
 * Requirements: 7.3
 */
public class StockFailedEvent extends Event<StockFailedPayload> {
    
    /**
     * Constructor for creating a new StockFailed event.
     * 
     * @param payload The stock failure data
     */
    public StockFailedEvent(StockFailedPayload payload) {
        super("StockFailed", payload);
    }
    
    /**
     * Constructor for deserialization.
     * 
     * @param eventId The unique identifier for this event
     * @param eventType The type of the event
     * @param timestamp The ISO 8601 timestamp when the event occurred
     * @param payload The stock failure data
     */
    @JsonCreator
    public StockFailedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("payload") StockFailedPayload payload) {
        super(eventId, eventType, timestamp, payload);
    }
}
