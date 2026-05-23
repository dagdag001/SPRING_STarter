package com.example.paymentservice.application.usecase;

import com.example.paymentservice.domain.entity.Payment;
import com.example.paymentservice.domain.repository.PaymentRepository;
import com.example.shared.event.PaymentCompletedEvent;
import com.example.shared.event.PaymentCompletedPayload;
import com.example.shared.event.PaymentFailedEvent;
import com.example.shared.event.PaymentFailedPayload;
import com.example.shared.messaging.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Use case for processing payments.
 * Implements mock payment processing logic with success/failure decision.
 * 
 * Requirements: 1.2, 6.1, 6.2, 6.3
 */
@Service
public class ProcessPaymentUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(ProcessPaymentUseCase.class);
    
    private final PaymentRepository paymentRepository;
    private final EventPublisher eventPublisher;
    
    @Value("${rabbitmq.routing-key.payment-completed:payment.completed}")
    private String paymentCompletedRoutingKey;
    
    @Value("${rabbitmq.routing-key.payment-failed:payment.failed}")
    private String paymentFailedRoutingKey;
    
    @Value("${payment.failure-threshold:1000.00}")
    private BigDecimal failureThreshold;
    
    public ProcessPaymentUseCase(PaymentRepository paymentRepository, EventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Processes a payment for an order.
     * Mock logic: payments fail if amount exceeds threshold (default 1000.00).
     * 
     * @param orderId The order ID
     * @param amount The payment amount
     */
    public void processPayment(String orderId, BigDecimal amount) {
        logger.info("Processing payment for order: {}, amount: {}", orderId, amount);
        
        // Check if payment already exists for this order
        if (paymentRepository.existsByOrderId(orderId)) {
            logger.warn("Payment already exists for order: {}", orderId);
            return;
        }
        
        String paymentId = UUID.randomUUID().toString();
        LocalDateTime processedAt = LocalDateTime.now();
        
        // Mock payment decision: fail if amount exceeds threshold
        boolean paymentSucceeds = amount.compareTo(failureThreshold) <= 0;
        
        if (paymentSucceeds) {
            // Payment succeeds
            Payment payment = new Payment(paymentId, orderId, amount, "COMPLETED", processedAt);
            paymentRepository.save(payment);
            
            logger.info("Payment successful for order: {}, paymentId: {}", orderId, paymentId);
            
            // Publish PaymentCompleted event
            PaymentCompletedPayload payload = new PaymentCompletedPayload(paymentId, orderId, amount);
            PaymentCompletedEvent event = new PaymentCompletedEvent(payload);
            eventPublisher.publish(event, paymentCompletedRoutingKey);
            
            logger.info("Published PaymentCompleted event for order: {}", orderId);
        } else {
            // Payment fails
            Payment payment = new Payment(paymentId, orderId, amount, "FAILED", processedAt);
            paymentRepository.save(payment);
            
            String reason = "Payment amount exceeds limit of " + failureThreshold;
            logger.warn("Payment failed for order: {}, reason: {}", orderId, reason);
            
            // Publish PaymentFailed event
            PaymentFailedPayload payload = new PaymentFailedPayload(orderId, reason);
            PaymentFailedEvent event = new PaymentFailedEvent(payload);
            eventPublisher.publish(event, paymentFailedRoutingKey);
            
            logger.info("Published PaymentFailed event for order: {}", orderId);
        }
    }
}
