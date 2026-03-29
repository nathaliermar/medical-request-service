package com.healthinsurance.medicalrequest.application.port.out;

import com.healthinsurance.medicalrequest.application.dto.RequestStatusChangedEvent;

/**
 * Output port for publishing domain events.
 * Abstracts messaging infrastructure to keep use cases testable without brokers.
 */
public interface EventPublisherPort {

    void publish(RequestStatusChangedEvent event);
}
