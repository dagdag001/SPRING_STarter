package com.example.paymentservice.infrastructure.persistence;

import com.example.paymentservice.domain.entity.Payment;
import com.example.paymentservice.domain.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of PaymentRepository using Spring Data JPA.
 * 
 * Requirements: 1.3, 11.3
 */
@Component
public class PaymentRepositoryImpl implements PaymentRepository {
    
    private final PaymentJpaRepository jpaRepository;
    
    public PaymentRepositoryImpl(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity entity = toJpaEntity(payment);
        PaymentJpaEntity savedEntity = jpaRepository.save(entity);
        return toDomainEntity(savedEntity);
    }
    
    @Override
    public Optional<Payment> findById(String paymentId) {
        return jpaRepository.findById(paymentId)
                .map(this::toDomainEntity);
    }
    
    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return jpaRepository.findByOrderId(orderId)
                .map(this::toDomainEntity);
    }
    
    @Override
    public boolean existsByOrderId(String orderId) {
        return jpaRepository.existsByOrderId(orderId);
    }
    
    private PaymentJpaEntity toJpaEntity(Payment payment) {
        return new PaymentJpaEntity(
            payment.getPaymentId(),
            payment.getOrderId(),
            payment.getAmount(),
            payment.getStatus(),
            payment.getProcessedAt()
        );
    }
    
    private Payment toDomainEntity(PaymentJpaEntity entity) {
        return new Payment(
            entity.getPaymentId(),
            entity.getOrderId(),
            entity.getAmount(),
            entity.getStatus(),
            entity.getProcessedAt()
        );
    }
}
