package com.ecommerce.payment.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String id;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime timestamp;

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED
    }
}
