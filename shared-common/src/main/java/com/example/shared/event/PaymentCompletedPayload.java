package com.example.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Payload for PaymentCompleted event.
 * Contains payment completion information.
 * 
 * Requirements: 6.4
 */
public class PaymentCompletedPayload {
    
    @JsonProperty("paymentId")
    @NotBlank(message = "paymentId cannot be blank")
    private String paymentId;
    
    @JsonProperty("orderId")
    @NotBlank(message = "orderId cannot be blank")
    private String orderId;
    
    @JsonProperty("amount")
    @NotNull(message = "amount cannot be null")
    @Min(value = 0, message = "amount must be non-negative")
    private BigDecimal amount;
    
    // Default constructor for Jackson
    public PaymentCompletedPayload() {
    }
    
    public PaymentCompletedPayload(String paymentId, String orderId, BigDecimal amount) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentCompletedPayload that = (PaymentCompletedPayload) o;
        return Objects.equals(paymentId, that.paymentId) &&
               Objects.equals(orderId, that.orderId) &&
               Objects.equals(amount, that.amount);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(paymentId, orderId, amount);
    }
    
    @Override
    public String toString() {
        return "PaymentCompletedPayload{" +
               "paymentId='" + paymentId + '\'' +
               ", orderId='" + orderId + '\'' +
               ", amount=" + amount +
               '}';
    }
}
