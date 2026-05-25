package com.example.paymentservice.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Queue configuration for Payment Service.
 * Defines payment-specific queues and bindings.
 * Base RabbitMQ beans (exchange, template, converter) are provided by shared-common module.
 * 
 * Requirements: 1.3, 11.3, 12.1, 12.4
 */
@Configuration
public class PaymentServiceQueueConfig {
    
    @Value("${rabbitmq.queue.order-created:payment.order.created.queue}")
    private String orderCreatedQueue;
    
    @Value("${rabbitmq.routing-key.order-created:order.created}")
    private String orderCreatedRoutingKey;
    
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(orderCreatedQueue, true);
    }
    
    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange appExchange) {
        return BindingBuilder
                .bind(orderCreatedQueue)
                .to(appExchange)
                .with(orderCreatedRoutingKey);
    }
}
