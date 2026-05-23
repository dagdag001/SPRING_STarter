package com.example.authservice.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for Auth Service.
 * 
 * Requirements: 1.3, 12.1, 12.2
 */
@Configuration
public class RabbitMQConfig {
    
    @Value("${rabbitmq.exchange.name:app.exchange}")
    private String exchangeName;
    
    @Value("${rabbitmq.queue.user-registered:auth.user.registered.queue}")
    private String userRegisteredQueue;
    
    @Value("${rabbitmq.routing-key.user-registered:user.registered}")
    private String userRegisteredRoutingKey;
    
    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(exchangeName, true, false);
    }
    
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
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setExchange(exchangeName);
        return template;
    }
}
