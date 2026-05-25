package com.example.shippingservice.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Queue configuration for Shipping Service.
 * Defines shipping-specific queues and bindings.
 * Base RabbitMQ beans (exchange, template, converter) are provided by shared-common module.
 * 
 * Requirements: 1.3, 11.5, 12.1, 12.6
 */
@Configuration
public class ShippingServiceQueueConfig {
    
    @Value("${rabbitmq.queue.payment-completed:shipping.payment.completed.queue}")
    private String paymentCompletedQueue;
    
    @Value("${rabbitmq.queue.stock-reserved:shipping.stock.reserved.queue}")
    private String stockReservedQueue;
    
    @Value("${rabbitmq.routing-key.payment-completed:payment.completed}")
    private String paymentCompletedRoutingKey;
    
    @Value("${rabbitmq.routing-key.stock-reserved:stock.reserved}")
    private String stockReservedRoutingKey;
    
    @Bean
    public Queue paymentCompletedQueue() {
        return new Queue(paymentCompletedQueue, true);
    }
    
    @Bean
    public Queue stockReservedQueue() {
        return new Queue(stockReservedQueue, true);
    }
    
    @Bean
    public Binding paymentCompletedBinding(Queue paymentCompletedQueue, TopicExchange appExchange) {
        return BindingBuilder
                .bind(paymentCompletedQueue)
                .to(appExchange)
                .with(paymentCompletedRoutingKey);
    }
    
    @Bean
    public Binding stockReservedBinding(Queue stockReservedQueue, TopicExchange appExchange) {
        return BindingBuilder
                .bind(stockReservedQueue)
                .to(appExchange)
                .with(stockReservedRoutingKey);
    }
}
