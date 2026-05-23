package com.example.paymentservice.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a payment.
 * 
 * Requirements: 1.1, 6.1
 */
public class Payment {
    
    private final String paymentId;
    private final String orderId;
    private final BigDecimal amount;
    private final String status;
    private final LocalDateTime processedAt;
    
    public Payment(String paymentId, String orderId, BigDecimal amount, String status, LocalDateTime processedAt) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new IllegalArgumentException("paymentId cannot be null or empty");
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("orderId cannot be null or empty");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be null or negative");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("status cannot be null or empty");
        }
        if (processedAt == null) {
            throw new IllegalArgumentException("processedAt cannot be null");
        }
        
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.processedAt = processedAt;
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId) &&
               Objects.equals(orderId, payment.orderId) &&
               Objects.equals(amount, payment.amount) &&
               Objects.equals(status, payment.status) &&
               Objects.equals(processedAt, payment.processedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(paymentId, orderId, amount, status, processedAt);
    }
    
    @Override
    public String toString() {
        return "Payment{" +
               "paymentId='" + paymentId + '\'' +
               ", orderId='" + orderId + '\'' +
               ", amount=" + amount +
               ", status='" + status + '\'' +
               ", processedAt=" + processedAt +
               '}';
    }
}
