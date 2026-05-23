package com.example.orderservice.infrastructure.config;

import com.example.orderservice.application.usecase.CreateOrderUseCase;
import com.example.orderservice.application.usecase.GetOrderUseCase;
import com.example.orderservice.domain.repository.OrderRepository;
import com.example.shared.messaging.EventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean configuration for Order Service.
 * Wires up use cases with their dependencies.
 * 
 * Requirements: 1.3
 */
@Configuration
public class BeanConfig {
    
    @Bean
    public CreateOrderUseCase createOrderUseCase(
            OrderRepository orderRepository,
            EventPublisher eventPublisher) {
        return new CreateOrderUseCase(orderRepository, eventPublisher);
    }
    
    @Bean
    public GetOrderUseCase getOrderUseCase(OrderRepository orderRepository) {
        return new GetOrderUseCase(orderRepository);
    }
}
