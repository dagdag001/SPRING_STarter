package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Event published when a user successfully registers.
 * 
 * Requirements: 4.2
 */
public class UserRegisteredEvent extends Event<UserRegisteredPayload> {
    
    /**
     * Constructor for creating a new UserRegistered event.
     * 
     * @param payload The user registration data
     */
    public UserRegisteredEvent(UserRegisteredPayload payload) {
        super("UserRegistered", payload);
    }
    
    /**
     * Constructor for deserialization.
     * 
     * @param eventId The unique identifier for this event
     * @param eventType The type of the event
     * @param timestamp The ISO 8601 timestamp when the event occurred
     * @param payload The user registration data
     */
    @JsonCreator
    public UserRegisteredEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("payload") UserRegisteredPayload payload) {
        super(eventId, eventType, timestamp, payload);
    }
}
