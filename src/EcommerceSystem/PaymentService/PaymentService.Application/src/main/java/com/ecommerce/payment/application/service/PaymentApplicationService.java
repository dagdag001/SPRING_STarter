package com.ecommerce.payment.application.service;

import com.ecommerce.payment.application.ports.PaymentEventPublisher;
import com.ecommerce.payment.application.usecase.ProcessPaymentUseCase;
import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.shared.messaging.event.PaymentCompletedEvent;
import com.ecommerce.shared.messaging.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentApplicationService implements ProcessPaymentUseCase {

    private final PaymentEventPublisher eventPublisher;

    @Override
    public Payment process(String orderId, BigDecimal amount) {
        // Mock payment logic
        boolean success = amount.compareTo(BigDecimal.valueOf(1000.0)) < 0;

        Payment payment = Payment.builder()
                .id(UUID.randomUUID().toString())
                .orderId(orderId)
                .amount(amount)
                .status(success ? Payment.PaymentStatus.COMPLETED : Payment.PaymentStatus.FAILED)
                .timestamp(LocalDateTime.now())
                .build();

        if (success) {
            eventPublisher.publish(new PaymentCompletedEvent(payment.getId(), orderId));
        } else {
            eventPublisher.publish(new PaymentFailedEvent(orderId, "Amount exceeds limit"));
        }

        return payment;
    }
}
