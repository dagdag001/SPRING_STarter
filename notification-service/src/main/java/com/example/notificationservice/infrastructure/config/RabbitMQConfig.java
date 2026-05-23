package com.example.notificationservice.infrastructure.config;

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
 * RabbitMQ configuration for Notification Service.
 * Configures queue, exchange, and binding for receiving ALL events using wildcard routing key "#".
 * 
 * Requirements: 1.3, 11.6, 12.1, 12.7
 */
@Configuration
public class RabbitMQConfig {
    
    @Value("${rabbitmq.exchange.name:app.exchange}")
    private String exchangeName;
    
    @Value("${rabbitmq.queue.all-events:notification.all.events.queue}")
    private String allEventsQueue;
    
    @Value("${rabbitmq.routing-key.all-events:#}")
    private String allEventsRoutingKey;
    
    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(exchangeName, true, false);
    }
    
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
