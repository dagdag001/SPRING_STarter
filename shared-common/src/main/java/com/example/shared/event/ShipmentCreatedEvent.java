package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Event published when a shipment is successfully created.
 * 
 * Requirements: 8.2
 */
public class ShipmentCreatedEvent extends Event<ShipmentCreatedPayload> {
    
    /**
     * Constructor for creating a new ShipmentCreated event.
     * 
     * @param payload The shipment creation data
     */
    public ShipmentCreatedEvent(ShipmentCreatedPayload payload) {
        super("ShipmentCreated", payload);
    }
    
    /**
     * Constructor for deserialization.
     * 
     * @param eventId The unique identifier for this event
     * @param eventType The type of the event
     * @param timestamp The ISO 8601 timestamp when the event occurred
     * @param payload The shipment creation data
     */
    @JsonCreator
    public ShipmentCreatedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("payload") ShipmentCreatedPayload payload) {
        super(eventId, eventType, timestamp, payload);
    }
}
