package com.ecommerce.payment.domain.repository;

import com.ecommerce.payment.domain.entity.Payment;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByOrderId(String orderId);
}
