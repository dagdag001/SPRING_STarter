package com.example.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Base RabbitMQ configuration class for all microservices.
 * Provides common beans for RabbitMQ messaging infrastructure.
 * 
 * Requirements: 3.1, 3.2, 3.4, 12.1
 */
@Configuration
public class RabbitMQConfig {
    
    public static final String EXCHANGE_NAME = "app.exchange";
    
    /**
     * Creates the main topic exchange for the application.
     * All events are published to this exchange and routed based on routing keys.
     * 
     * @return TopicExchange bean for "app.exchange"
     */
    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }
    
    /**
     * Creates a Jackson2JsonMessageConverter for serializing/deserializing events.
     * Configures ObjectMapper with JavaTimeModule for proper Instant/LocalDateTime handling.
     * 
     * @return Jackson2JsonMessageConverter bean
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }
    
    /**
     * Creates a RabbitTemplate configured with JSON message converter.
     * Used by services to publish events to RabbitMQ.
     * 
     * @param connectionFactory The RabbitMQ connection factory
     * @return RabbitTemplate bean configured with JSON converter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
