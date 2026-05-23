package com.example.paymentservice.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity for Payment.
 * 
 * Requirements: 1.3, 11.3
 */
@Entity
@Table(name = "payments")
public class PaymentJpaEntity {
    
    @Id
    @Column(name = "payment_id", length = 36, nullable = false)
    private String paymentId;
    
    @Column(name = "order_id", length = 36, nullable = false, unique = true)
    private String orderId;
    
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "status", length = 20, nullable = false)
    private String status;
    
    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;
    
    // Default constructor for JPA
    protected PaymentJpaEntity() {
    }
    
    public PaymentJpaEntity(String paymentId, String orderId, BigDecimal amount, String status, LocalDateTime processedAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.processedAt = processedAt;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentJpaEntity that = (PaymentJpaEntity) o;
        return Objects.equals(paymentId, that.paymentId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }
}
