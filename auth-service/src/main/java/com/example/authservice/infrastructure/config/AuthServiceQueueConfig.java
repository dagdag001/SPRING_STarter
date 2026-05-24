package com.example.authservice.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Queue and binding configuration specific to Auth Service.
 * Base RabbitMQ configuration is provided by shared-common module.
 * 
 * Requirements: 1.3, 12.1, 12.2
 */
@Configuration
public class AuthServiceQueueConfig {
    
    @Value("${rabbitmq.queue.user-registered:auth.user.registered.queue}")
    private String userRegisteredQueue;
    
    @Value("${rabbitmq.routing-key.user-registered:user.registered}")
    private String userRegisteredRoutingKey;
    
    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(userRegisteredQueue, true);
    }
    
    @Bean
    public Binding userRegisteredBinding(Queue userRegisteredQueue, TopicExchange appExchange) {
        return BindingBuilder
                .bind(userRegisteredQueue)
                .to(appExchange)
                .with(userRegisteredRoutingKey);
    }
}
