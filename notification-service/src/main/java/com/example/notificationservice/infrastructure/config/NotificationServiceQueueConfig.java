package com.example.notificationservice.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Queue configuration for Notification Service.
 * Defines notification-specific queues and bindings.
 * Uses wildcard routing key "#" to receive ALL events.
 * Base RabbitMQ beans (exchange, template, converter) are provided by shared-common module.
 * 
 * Requirements: 1.3, 11.6, 12.1, 12.7
 */
@Configuration
public class NotificationServiceQueueConfig {
    
    @Value("${rabbitmq.queue.all-events:notification.all.events.queue}")
    private String allEventsQueue;
    
    @Value("${rabbitmq.routing-key.all-events:#}")
    private String allEventsRoutingKey;
    
    @Bean
    public Queue allEventsQueue() {
        return new Queue(allEventsQueue, true);
    }
    
    @Bean
    public Binding allEventsBinding(Queue allEventsQueue, TopicExchange appExchange) {
        return BindingBuilder
                .bind(allEventsQueue)
                .to(appExchange)
                .with(allEventsRoutingKey);
    }
}
