package com.healthinsurance.medicalrequest.application.usecase;

import com.healthinsurance.medicalrequest.application.dto.RequestStatusChangedEvent;
import com.healthinsurance.medicalrequest.application.port.in.SubmitRequestUseCase;
import com.healthinsurance.medicalrequest.application.port.out.EventPublisherPort;
import com.healthinsurance.medicalrequest.application.port.out.MedicalRequestRepository;
import com.healthinsurance.medicalrequest.domain.exception.RequestNotFoundException;
import com.healthinsurance.medicalrequest.domain.model.MedicalRequest;
import com.healthinsurance.medicalrequest.domain.model.RequestStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class SubmitRequestUseCaseImpl implements SubmitRequestUseCase {

    private final MedicalRequestRepository repository;
    private final EventPublisherPort eventPublisher;

    @Override
    public MedicalRequest submit(UUID requestId) {
        MedicalRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));

        RequestStatus previousStatus = request.getStatus();
        request.transitionTo(RequestStatus.SUBMITTED);

        MedicalRequest saved = repository.save(request);

        // The EventPublisherPort abstracts the messaging infrastructure.
        // The actual RabbitMQ publisher uses @TransactionalEventListener(AFTER_COMMIT)
        // to ensure the message is only sent after the DB transaction succeeds.
        eventPublisher.publish(RequestStatusChangedEvent.of(
                saved.getId(), saved.getBeneficiaryId(), previousStatus, saved.getStatus()
        ));

        log.info("Request id={} submitted for review", requestId);
        return saved;
    }
}
