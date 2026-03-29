package com.healthinsurance.medicalrequest.application.usecase;

import com.healthinsurance.medicalrequest.application.dto.RequestStatusChangedEvent;
import com.healthinsurance.medicalrequest.application.port.in.ApproveRequestUseCase;
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
public class ApproveRequestUseCaseImpl implements ApproveRequestUseCase {

    private final MedicalRequestRepository repository;
    private final EventPublisherPort eventPublisher;

    @Override
    public MedicalRequest approve(UUID requestId, boolean approved) {
        MedicalRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId));

        RequestStatus previousStatus = request.getStatus();
        RequestStatus newStatus = approved ? RequestStatus.APPROVED : RequestStatus.REJECTED;
        request.transitionTo(newStatus);

        MedicalRequest saved = repository.save(request);

        eventPublisher.publish(RequestStatusChangedEvent.of(
                saved.getId(), saved.getBeneficiaryId(), previousStatus, saved.getStatus()
        ));

        log.info("Request id={} {}", requestId, approved ? "approved" : "rejected");
        return saved;
    }
}
