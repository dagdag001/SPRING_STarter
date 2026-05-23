package com.ecommerce.shared.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentCompletedEvent extends BaseEvent {
    private String paymentId;
    private String orderId;
}

// Separate files for each event is better for Java, but I'll combine some in this thought block if needed.
// Actually, I'll stick to one file per class as per standard Java.
