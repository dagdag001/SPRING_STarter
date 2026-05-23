package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Event published when a payment fails.
 * 
 * Requirements: 6.3
 */
public class PaymentFailedEvent extends Event<PaymentFailedPayload> {
    
    /**
     * Constructor for creating a new PaymentFailed event.
     * 
     * @param payload The payment failure data
     */
    public PaymentFailedEvent(PaymentFailedPayload payload) {
        super("PaymentFailed", payload);
    }
    
    /**
     * Constructor for deserialization.
     * 
     * @param eventId The unique identifier for this event
     * @param eventType The type of the event
     * @param timestamp The ISO 8601 timestamp when the event occurred
     * @param payload The payment failure data
     */
    @JsonCreator
    public PaymentFailedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("payload") PaymentFailedPayload payload) {
        super(eventId, eventType, timestamp, payload);
    }
}
