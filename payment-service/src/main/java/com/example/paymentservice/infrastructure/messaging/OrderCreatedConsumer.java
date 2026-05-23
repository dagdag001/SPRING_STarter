package com.example.paymentservice.infrastructure.messaging;

import com.example.paymentservice.application.usecase.ProcessPaymentUseCase;
import com.example.shared.event.OrderCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumer for OrderCreated events.
 * Listens to order.created routing key and processes payments.
 * Uses manual acknowledgment mode for fine-grained control over message processing.
 * 
 * Requirements: 1.3, 3.5, 13.4, 13.5
 */
@Component
public class OrderCreatedConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderCreatedConsumer.class);
    
    private final ProcessPaymentUseCase processPaymentUseCase;
    private final ObjectMapper objectMapper;
    
    public OrderCreatedConsumer(ProcessPaymentUseCase processPaymentUseCase, ObjectMapper objectMapper) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Handles OrderCreated events from RabbitMQ with manual acknowledgment.
     * 
     * Acknowledgment strategy:
     * - Success: Acknowledge message (basicAck)
     * - Deserialization error: Reject message without requeue (basicReject with requeue=false)
     * - Business logic error: Reject message with requeue (basicNack with requeue=true)
     * 
     * @param message The raw message from RabbitMQ
     * @param channel The RabbitMQ channel for acknowledgment
     * @param deliveryTag The delivery tag for this message
     */
    @RabbitListener(queues = "${rabbitmq.queue.order-created:payment.order.created.queue}", ackMode = "MANUAL")
    public void handleOrderCreated(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        String messageBody = new String(message.getBody());
        logger.info("Received OrderCreated event: {}", messageBody);
        
        try {
            // Deserialize the event
            OrderCreatedEvent event = objectMapper.readValue(messageBody, OrderCreatedEvent.class);
            
            logger.info("Deserialized OrderCreated event - eventId: {}, orderId: {}", 
                       event.getEventId(), event.getPayload().getOrderId());
            
            try {
                // Process the payment
                processPaymentUseCase.processPayment(
                    event.getPayload().getOrderId(),
                    event.getPayload().getTotalAmount()
                );
                
                logger.info("Successfully processed payment for order: {}", event.getPayload().getOrderId());
                
                // Acknowledge message on successful processing
                channel.basicAck(deliveryTag, false);
                logger.debug("Message acknowledged: deliveryTag={}", deliveryTag);
                
            } catch (Exception e) {
                // Business logic error - log and reject with requeue for retry
                logger.error("Business logic error processing payment for order: {}, eventId: {}, error: {}", 
                           event.getPayload().getOrderId(), event.getEventId(), e.getMessage(), e);
                
                // Reject message with requeue=true to allow retry
                channel.basicNack(deliveryTag, false, true);
                logger.warn("Message rejected with requeue: deliveryTag={}", deliveryTag);
            }
            
        } catch (JsonProcessingException e) {
            // Deserialization error - log and reject message without requeue
            logger.error("Failed to deserialize OrderCreated event. Raw message: {}, error: {}", 
                       messageBody, e.getMessage(), e);
            
            try {
                // Reject message without requeue to prevent poison messages from blocking the queue
                channel.basicReject(deliveryTag, false);
                logger.warn("Message rejected without requeue due to deserialization error: deliveryTag={}", deliveryTag);
            } catch (IOException ioException) {
                logger.error("Failed to reject message: deliveryTag={}, error: {}", 
                           deliveryTag, ioException.getMessage(), ioException);
            }
            
        } catch (IOException e) {
            // Channel communication error
            logger.error("Failed to acknowledge/reject message: deliveryTag={}, error: {}", 
                       deliveryTag, e.getMessage(), e);
        }
    }
}
