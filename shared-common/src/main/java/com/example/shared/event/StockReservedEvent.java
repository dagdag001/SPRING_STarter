package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Event published when stock is successfully reserved for an order.
 * 
 * Requirements: 7.2
 */
public class StockReservedEvent extends Event<StockReservedPayload> {
    
    /**
     * Constructor for creating a new StockReserved event.
     * 
     * @param payload The stock reservation data
     */
    public StockReservedEvent(StockReservedPayload payload) {
        super("StockReserved", payload);
    }
    
    /**
     * Constructor for deserialization.
     * 
     * @param eventId The unique identifier for this event
     * @param eventType The type of the event
     * @param timestamp The ISO 8601 timestamp when the event occurred
     * @param payload The stock reservation data
     */
    @JsonCreator
    public StockReservedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("payload") StockReservedPayload payload) {
        super(eventId, eventType, timestamp, payload);
    }
}
