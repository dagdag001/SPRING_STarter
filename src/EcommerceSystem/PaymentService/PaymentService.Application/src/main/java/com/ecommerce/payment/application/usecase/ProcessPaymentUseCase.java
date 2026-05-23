package com.ecommerce.payment.application.usecase;

import com.ecommerce.payment.domain.entity.Payment;
import java.math.BigDecimal;

public interface ProcessPaymentUseCase {
    Payment process(String orderId, BigDecimal amount);
}
