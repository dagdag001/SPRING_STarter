package com.example.orderservice.infrastructure.config;

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
 * RabbitMQ configuration for Order Service.
 * 
 * Requirements: 1.3, 12.1, 12.3
 */
@Configuration
public class RabbitMQConfig {
    
    @Value("${rabbitmq.exchange.name:app.exchange}")
    private String exchangeName;
    
    @Value("${rabbitmq.queue.order-created:order.created.queue}")
    private String orderCreatedQueue;
    
    @Value("${rabbitmq.routing-key.order-created:order.created}")
    private String orderCreatedRoutingKey;
    
    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(exchangeName, true, false);
    }
    
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
