package com.example.notificationservice.infrastructure.messaging;

import com.example.notificationservice.application.usecase.LogNotificationUseCase;
import com.example.shared.event.Event;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumer that listens to ALL system events using wildcard routing key "#".
 * Logs all events to the notification log for monitoring and auditing.
 * Uses manual acknowledgment mode for fine-grained control over message processing.
 * 
 * Requirements: 1.3, 3.5, 9.8, 12.7, 13.4, 13.5
 */
@Component
public class AllEventsConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(AllEventsConsumer.class);
    
    private final LogNotificationUseCase logNotificationUseCase;
    
    public AllEventsConsumer(LogNotificationUseCase logNotificationUseCase) {
        this.logNotificationUseCase = logNotificationUseCase;
    }
    
    /**
     * Listens to all events from the notification queue with manual acknowledgment.
     * The queue is bound to routing key "#" to receive all events.
     * 
     * Acknowledgment strategy:
     * - Success: Acknowledge message (basicAck)
     * - Deserialization error: Reject message without requeue (basicReject with requeue=false)
     * - Business logic error: Reject message with requeue (basicNack with requeue=true)
     * 
     * @param event The event received from RabbitMQ
     * @param channel The RabbitMQ channel for acknowledgment
     * @param deliveryTag The delivery tag for this message
     */
    @RabbitListener(queues = "${rabbitmq.queue.all-events:notification.all.events.queue}", ackMode = "MANUAL")
    public void handleAllEvents(@Payload Event<?> event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            logger.info("Received event: eventType={}, eventId={}", 
                       event.getEventType(), event.getEventId());
            
            try {
                // Log the event using the use case
                logNotificationUseCase.logEvent(event.getEventType(), event);
                
                logger.info("Successfully logged event: eventType={}, eventId={}", 
                           event.getEventType(), event.getEventId());
                
                // Acknowledge message on successful processing
                channel.basicAck(deliveryTag, false);
                logger.debug("Message acknowledged: deliveryTag={}", deliveryTag);
                
            } catch (Exception e) {
                // Business logic error - log and reject with requeue for retry
                logger.error("Business logic error processing event: eventType={}, eventId={}, error={}", 
                            event.getEventType(), event.getEventId(), e.getMessage(), e);
                
                // Reject message with requeue=true to allow retry
                channel.basicNack(deliveryTag, false, true);
                logger.warn("Message rejected with requeue: deliveryTag={}", deliveryTag);
            }
            
        } catch (Exception e) {
            // Deserialization or other error - log and reject message without requeue
            logger.error("Failed to process event, error: {}", e.getMessage(), e);
            
            try {
                // Reject message without requeue to prevent poison messages from blocking the queue
                channel.basicReject(deliveryTag, false);
                logger.warn("Message rejected without requeue due to processing error: deliveryTag={}", deliveryTag);
            } catch (IOException ioException) {
                logger.error("Failed to reject message: deliveryTag={}, error: {}", 
                           deliveryTag, ioException.getMessage(), ioException);
            }
        }
    }
}
