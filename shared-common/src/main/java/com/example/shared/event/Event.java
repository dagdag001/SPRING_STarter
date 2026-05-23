package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all events in the system.
 * Events are immutable messages representing something that happened in the system.
 * 
 * Requirements: 10.1, 10.2, 10.3, 10.4, 10.5
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "eventType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = UserRegisteredEvent.class, name = "UserRegistered"),
    @JsonSubTypes.Type(value = OrderCreatedEvent.class, name = "OrderCreated"),
    @JsonSubTypes.Type(value = PaymentCompletedEvent.class, name = "PaymentCompleted"),
    @JsonSubTypes.Type(value = PaymentFailedEvent.class, name = "PaymentFailed"),
    @JsonSubTypes.Type(value = StockReservedEvent.class, name = "StockReserved"),
    @JsonSubTypes.Type(value = StockFailedEvent.class, name = "StockFailed"),
    @JsonSubTypes.Type(value = ShipmentCreatedEvent.class, name = "ShipmentCreated")
})
public abstract class Event<T> {
    
    @JsonProperty("eventId")
    private final String eventId;
    
    @JsonProperty("eventType")
    private final String eventType;
    
    @JsonProperty("timestamp")
    private final String timestamp;
    
    @JsonProperty("payload")
    private final T payload;
    
    /**
     * Constructor for creating a new event with generated ID and current timestamp.
     * 
     * @param eventType The type of the event
     * @param payload The event-specific data
     */
    protected Event(String eventType, T payload) {
        this(UUID.randomUUID().toString(), eventType, Instant.now().toString(), payload);
    }
    
    /**
     * Constructor for creating an event with all fields specified.
     * Used primarily for deserialization.
     * 
     * @param eventId The unique identifier for this event
     * @param eventType The type of the event
     * @param timestamp The ISO 8601 timestamp when the event occurred
     * @param payload The event-specific data
     */
    protected Event(String eventId, String eventType, String timestamp, T payload) {
        if (eventId == null || eventId.trim().isEmpty()) {
            throw new IllegalArgumentException("eventId cannot be null or empty");
        }
        if (eventType == null || eventType.trim().isEmpty()) {
            throw new IllegalArgumentException("eventType cannot be null or empty");
        }
        if (timestamp == null || timestamp.trim().isEmpty()) {
            throw new IllegalArgumentException("timestamp cannot be null or empty");
        }
        if (payload == null) {
            throw new IllegalArgumentException("payload cannot be null");
        }
        
        this.eventId = eventId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.payload = payload;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public T getPayload() {
        return payload;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event<?> event = (Event<?>) o;
        return Objects.equals(eventId, event.eventId) &&
               Objects.equals(eventType, event.eventType) &&
               Objects.equals(timestamp, event.timestamp) &&
               Objects.equals(payload, event.payload);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventType, timestamp, payload);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "eventId='" + eventId + '\'' +
               ", eventType='" + eventType + '\'' +
               ", timestamp='" + timestamp + '\'' +
               ", payload=" + payload +
               '}';
    }
}
