package com.example.shared.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for RabbitMQConfig.
 * Tests bean creation and configuration.
 */
class RabbitMQConfigTest {
    
    private final RabbitMQConfig config = new RabbitMQConfig();
    
    @Test
    void appExchange_ShouldCreateTopicExchange() {
        // Act
        TopicExchange exchange = config.appExchange();
        
        // Assert
        assertNotNull(exchange);
        assertEquals("app.exchange", exchange.getName());
        assertTrue(exchange.isDurable());
        assertFalse(exchange.isAutoDelete());
    }
    
    @Test
    void messageConverter_ShouldCreateJackson2JsonMessageConverter() {
        // Act
        Jackson2JsonMessageConverter converter = config.messageConverter();
        
        // Assert
        assertNotNull(converter);
    }
    
    @Test
    void rabbitTemplate_ShouldCreateRabbitTemplateWithJsonConverter() {
        // Arrange
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        
        // Act
        RabbitTemplate rabbitTemplate = config.rabbitTemplate(connectionFactory);
        
        // Assert
        assertNotNull(rabbitTemplate);
        assertNotNull(rabbitTemplate.getMessageConverter());
        assertTrue(rabbitTemplate.getMessageConverter() instanceof Jackson2JsonMessageConverter);
    }
    
    @Test
    void exchangeName_ShouldBeAppExchange() {
        // Assert
        assertEquals("app.exchange", RabbitMQConfig.EXCHANGE_NAME);
    }
}
