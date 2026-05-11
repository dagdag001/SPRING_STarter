package com.ecommerce.order.application.service;

import com.ecommerce.order.application.dto.CreateOrderRequest;
import com.ecommerce.order.application.ports.EventPublisher;
import com.ecommerce.order.application.usecase.CreateOrderUseCase;
import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderItem;
import com.ecommerce.order.domain.repository.OrderRepository;
import com.ecommerce.shared.messaging.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderApplicationServiceImpl implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    @Override
    public Order execute(CreateOrderRequest request) {
        List<OrderItem> items = request.getItems().stream()
                .map(item -> OrderItem.builder()
                        .id(UUID.randomUUID().toString())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalAmount = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .userId(request.getUserId())
                .items(items)
                .totalAmount(totalAmount)
                .status(Order.OrderStatus.CREATED)
                .build();

        Order savedOrder = orderRepository.save(order);

        eventPublisher.publish(new OrderCreatedEvent(savedOrder.getId(), savedOrder.getUserId(), savedOrder.getTotalAmount()));

        return savedOrder;
    }
}
