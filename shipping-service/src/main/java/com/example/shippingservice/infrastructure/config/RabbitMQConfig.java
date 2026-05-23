package com.example.shippingservice.infrastructure.config;

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
 * RabbitMQ configuration for Shipping Service.
 * Configures queues, exchanges, and bindings for event-driven communication.
 * 
 * Requirements: 1.3, 11.5, 12.1, 12.6
 */
@Configuration
public class RabbitMQConfig {
    
    @Value("${rabbitmq.exchange.name:app.exchange}")
    private String exchangeName;
    
    @Value("${rabbitmq.queue.payment-completed:shipping.payment.completed.queue}")
    private String paymentCompletedQueue;
    
    @Value("${rabbitmq.queue.stock-reserved:shipping.stock.reserved.queue}")
    private String stockReservedQueue;
    
    @Value("${rabbitmq.routing-key.payment-completed:payment.completed}")
    private String paymentCompletedRoutingKey;
    
    @Value("${rabbitmq.routing-key.stock-reserved:stock.reserved}")
    private String stockReservedRoutingKey;
    
    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(exchangeName, true, false);
    }
    
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
