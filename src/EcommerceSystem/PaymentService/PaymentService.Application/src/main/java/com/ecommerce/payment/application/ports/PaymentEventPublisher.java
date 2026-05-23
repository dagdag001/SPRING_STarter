package com.ecommerce.payment.application.ports;

import com.ecommerce.shared.messaging.event.BaseEvent;

public interface PaymentEventPublisher {
    void publish(BaseEvent event);
}
