package com.ecommerce.order.domain.entity;

import com.ecommerce.shared.domain.model.AggregateRoot;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Order extends AggregateRoot {
    private String id;
    private String userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItem> items;

    public enum OrderStatus {
        CREATED, PAID, STOCK_RESERVED, SHIPPED, CANCELLED
    }
}
