package com.healthinsurance.medicalrequest.application.dto;

import com.healthinsurance.medicalrequest.domain.model.RequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published whenever a MedicalRequest changes status.
 * This is a plain data record — no framework annotations. It travels through
 * the EventPublisherPort interface so use cases never import messaging libs.
 */
@Getter
@Builder
public class RequestStatusChangedEvent {

    private UUID requestId;
    private UUID beneficiaryId;
    private RequestStatus previousStatus;
    private RequestStatus newStatus;
    private LocalDateTime occurredAt;

    public static RequestStatusChangedEvent of(UUID requestId, UUID beneficiaryId,
                                               RequestStatus previous, RequestStatus next) {
        return RequestStatusChangedEvent.builder()
                .requestId(requestId)
                .beneficiaryId(beneficiaryId)
                .previousStatus(previous)
                .newStatus(next)
                .occurredAt(LocalDateTime.now())
                .build();
    }
}
