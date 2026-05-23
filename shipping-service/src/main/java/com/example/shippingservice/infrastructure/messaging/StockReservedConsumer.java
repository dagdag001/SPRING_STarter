package com.example.shippingservice.infrastructure.messaging;

import com.example.shared.event.StockReservedEvent;
import com.example.shippingservice.application.usecase.CorrelateOrderEventsUseCase;
import com.example.shippingservice.application.usecase.CreateShipmentUseCase;
import com.example.shippingservice.domain.entity.OrderConfirmation;
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
 * Consumer for StockReserved events.
 * Listens to stock.reserved routing key and correlates with payment confirmations.
 * Uses manual acknowledgment mode for fine-grained control over message processing.
 * 
 * Requirements: 1.3, 3.5, 8.1, 8.4, 8.5, 13.4, 13.5, 22.1, 22.2, 22.3
 */
@Component
public class StockReservedConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(StockReservedConsumer.class);
    
    private final CorrelateOrderEventsUseCase correlateOrderEventsUseCase;
    private final CreateShipmentUseCase createShipmentUseCase;
    private final ObjectMapper objectMapper;
    
    public StockReservedConsumer(
            CorrelateOrderEventsUseCase correlateOrderEventsUseCase,
            CreateShipmentUseCase createShipmentUseCase,
            ObjectMapper objectMapper) {
        this.correlateOrderEventsUseCase = correlateOrderEventsUseCase;
        this.createShipmentUseCase = createShipmentUseCase;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Handles StockReserved events from RabbitMQ with manual acknowledgment.
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
    @RabbitListener(queues = "${rabbitmq.queue.stock-reserved:shipping.stock.reserved.queue}", ackMode = "MANUAL")
    public void handleStockReserved(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        String messageBody = new String(message.getBody());
        logger.info("Received StockReserved event: {}", messageBody);
        
        try {
            // Deserialize the event
            StockReservedEvent event = objectMapper.readValue(messageBody, StockReservedEvent.class);
            
            logger.info("Deserialized StockReserved event - eventId: {}, orderId: {}", 
                       event.getEventId(), event.getPayload().getOrderId());
            
            try {
                String orderId = event.getPayload().getOrderId();
                
                // Record stock confirmation
                OrderConfirmation confirmation = correlateOrderEventsUseCase.recordStockConfirmation(orderId);
                
                logger.info("Recorded stock confirmation for order: {}, bothConfirmed: {}", 
                           orderId, confirmation.isBothConfirmed());
                
                // Check if both confirmations are received
                if (confirmation.isBothConfirmed()) {
                    logger.info("Both confirmations received for order: {}, creating shipment", orderId);
                    createShipmentUseCase.createShipment(orderId);
                } else {
                    logger.info("Waiting for payment confirmation for order: {}", orderId);
                }
                
                // Acknowledge message on successful processing
                channel.basicAck(deliveryTag, false);
                logger.debug("Message acknowledged: deliveryTag={}", deliveryTag);
                
            } catch (Exception e) {
                // Business logic error - log and reject with requeue for retry
                logger.error("Business logic error processing StockReserved event for order: {}, eventId: {}, error: {}", 
                           event.getPayload().getOrderId(), event.getEventId(), e.getMessage(), e);
                
                // Reject message with requeue=true to allow retry
                channel.basicNack(deliveryTag, false, true);
                logger.warn("Message rejected with requeue: deliveryTag={}", deliveryTag);
            }
            
        } catch (JsonProcessingException e) {
            // Deserialization error - log and reject message without requeue
            logger.error("Failed to deserialize StockReserved event. Raw message: {}, error: {}", 
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
