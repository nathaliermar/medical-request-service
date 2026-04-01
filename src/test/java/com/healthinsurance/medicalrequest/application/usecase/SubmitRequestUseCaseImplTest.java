package com.healthinsurance.medicalrequest.application.usecase;

import com.healthinsurance.medicalrequest.application.dto.RequestStatusChangedEvent;
import com.healthinsurance.medicalrequest.application.port.out.EventPublisherPort;
import com.healthinsurance.medicalrequest.application.port.out.MedicalRequestRepository;
import com.healthinsurance.medicalrequest.domain.exception.RequestNotFoundException;
import com.healthinsurance.medicalrequest.domain.model.MedicalProcedure;
import com.healthinsurance.medicalrequest.domain.model.MedicalRequest;
import com.healthinsurance.medicalrequest.domain.model.RequestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitRequestUseCaseImplTest {

    @Mock
    private MedicalRequestRepository repository;

    @Mock
    private EventPublisherPort eventPublisher;

    @InjectMocks
    private SubmitRequestUseCaseImpl useCase;

    private UUID requestId;
    private UUID beneficiaryId;
    private MedicalRequest draftRequest;

    @BeforeEach
    void setUp() {
        requestId = UUID.randomUUID();
        beneficiaryId = UUID.randomUUID();

        MedicalProcedure procedure = MedicalProcedure.create(
                UUID.randomUUID(),
                "ICD10-A00",
                "CBO-225125",
                "Test Procedure",
                1
        );
        procedure.markCoverageApproved();

        draftRequest = MedicalRequest.reconstitute(
                requestId,
                beneficiaryId,
                "Test Request",
                RequestStatus.DRAFT,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                List.of(procedure)
        );
    }

    @Test
    void submit_WithDraftRequest_ShouldTransitionToSubmitted() {
        when(repository.findById(requestId)).thenReturn(Optional.of(draftRequest));
        when(repository.save(any(MedicalRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MedicalRequest result = useCase.submit(requestId);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(RequestStatus.SUBMITTED);
        assertThat(result.getSubmittedAt()).isNotNull();
        
        verify(repository).findById(requestId);
        verify(repository).save(any(MedicalRequest.class));
    }

    @Test
    void submit_ShouldPublishStatusChangedEvent() {
        when(repository.findById(requestId)).thenReturn(Optional.of(draftRequest));
        when(repository.save(any(MedicalRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.submit(requestId);

        ArgumentCaptor<RequestStatusChangedEvent> eventCaptor = ArgumentCaptor.forClass(RequestStatusChangedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());

        RequestStatusChangedEvent event = eventCaptor.getValue();
        assertThat(event.getRequestId()).isEqualTo(requestId);
        assertThat(event.getBeneficiaryId()).isEqualTo(beneficiaryId);
        assertThat(event.getPreviousStatus()).isEqualTo(RequestStatus.DRAFT);
        assertThat(event.getNewStatus()).isEqualTo(RequestStatus.SUBMITTED);
    }

    @Test
    void submit_WithNonExistentRequest_ShouldThrowException() {
        when(repository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.submit(requestId))
                .isInstanceOf(RequestNotFoundException.class);

        verify(repository).findById(requestId);
        verify(repository, never()).save(any(MedicalRequest.class));
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void submit_ShouldSaveRequestBeforePublishingEvent() {
        when(repository.findById(requestId)).thenReturn(Optional.of(draftRequest));
        when(repository.save(any(MedicalRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.submit(requestId);

        var inOrder = inOrder(repository, eventPublisher);
        inOrder.verify(repository).findById(requestId);
        inOrder.verify(repository).save(any(MedicalRequest.class));
        inOrder.verify(eventPublisher).publish(any(RequestStatusChangedEvent.class));
    }
}
