package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Event published when a payment is successfully completed.
 * 
 * Requirements: 6.2
 */
public class PaymentCompletedEvent extends Event<PaymentCompletedPayload> {
    
    /**
     * Constructor for creating a new PaymentCompleted event.
     * 
     * @param payload The payment completion data
     */
    public PaymentCompletedEvent(PaymentCompletedPayload payload) {
        super("PaymentCompleted", payload);
    }
    
    /**
     * Constructor for deserialization.
     * 
     * @param eventId The unique identifier for this event
     * @param eventType The type of the event
     * @param timestamp The ISO 8601 timestamp when the event occurred
     * @param payload The payment completion data
     */
    @JsonCreator
    public PaymentCompletedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("payload") PaymentCompletedPayload payload) {
        super(eventId, eventType, timestamp, payload);
    }
}
