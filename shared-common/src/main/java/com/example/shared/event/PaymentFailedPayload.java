package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * Payload for PaymentFailed event.
 * Contains payment failure information.
 * 
 * Requirements: 6.5
 */
public class PaymentFailedPayload {
    
    @JsonProperty("orderId")
    @NotBlank(message = "orderId cannot be blank")
    private String orderId;
    
    @JsonProperty("reason")
    @NotBlank(message = "reason cannot be blank")
    private String reason;
    
    // Default constructor for Jackson
    public PaymentFailedPayload() {
    }
    
    public PaymentFailedPayload(String orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentFailedPayload that = (PaymentFailedPayload) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(reason, that.reason);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId, reason);
    }
    
    @Override
    public String toString() {
        return "PaymentFailedPayload{" +
               "orderId='" + orderId + '\'' +
               ", reason='" + reason + '\'' +
               '}';
    }
}
