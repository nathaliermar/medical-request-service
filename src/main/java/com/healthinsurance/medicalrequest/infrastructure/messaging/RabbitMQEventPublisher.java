package com.healthinsurance.medicalrequest.infrastructure.messaging;

import com.healthinsurance.medicalrequest.application.dto.RequestStatusChangedEvent;
import com.healthinsurance.medicalrequest.application.port.out.EventPublisherPort;
import com.healthinsurance.medicalrequest.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * RabbitMQ adapter implementing EventPublisherPort.
 * Uses @TransactionalEventListener to publish events only after transaction commit.
 * Falls back to immediate publishing when no transaction is active.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;
    private final ApplicationEventPublisher springEventPublisher;

    @Override
    public void publish(RequestStatusChangedEvent event) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            springEventPublisher.publishEvent(
                    new RequestStatusChangedApplicationEvent(this, event));
        } else {
            log.warn("No active transaction when publishing event for requestId={}. " +
                     "Publishing immediately (not transactionally).", event.getRequestId());
            sendToRabbit(event);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCommit(RequestStatusChangedApplicationEvent applicationEvent) {
        sendToRabbit(applicationEvent.getDomainEvent());
    }

    private void sendToRabbit(RequestStatusChangedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    event
            );
            log.info("Event published requestId={}, {} → {}",
                    event.getRequestId(), event.getPreviousStatus(), event.getNewStatus());
        } catch (Exception ex) {
            log.error("Failed to publish event for requestId={}: {}",
                    event.getRequestId(), ex.getMessage(), ex);
        }
    }

    /** Internal wrapper keeps domain event classes free of Spring types. */
    public static class RequestStatusChangedApplicationEvent
            extends org.springframework.context.ApplicationEvent {

        private final RequestStatusChangedEvent domainEvent;

        public RequestStatusChangedApplicationEvent(Object source,
                                                     RequestStatusChangedEvent domainEvent) {
            super(source);
            this.domainEvent = domainEvent;
        }

        public RequestStatusChangedEvent getDomainEvent() {
            return domainEvent;
        }
    }
}
