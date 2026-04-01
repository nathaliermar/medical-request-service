package com.healthinsurance.medicalrequest.infrastructure.web.controller;

import com.healthinsurance.medicalrequest.application.port.in.ApproveRequestUseCase;
import com.healthinsurance.medicalrequest.application.port.in.CreateRequestUseCase;
import com.healthinsurance.medicalrequest.application.port.in.SubmitRequestUseCase;
import com.healthinsurance.medicalrequest.application.dto.CreateRequestCommand;
import com.healthinsurance.medicalrequest.domain.model.MedicalProcedure;
import com.healthinsurance.medicalrequest.domain.model.MedicalRequest;
import com.healthinsurance.medicalrequest.domain.model.RequestStatus;
import com.healthinsurance.medicalrequest.infrastructure.web.dto.request.CreateRequestRequest;
import com.healthinsurance.medicalrequest.infrastructure.web.dto.response.MedicalRequestResponse;
import com.healthinsurance.medicalrequest.infrastructure.web.mapper.MedicalRequestWebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRequestControllerTest {

    @Mock
    private CreateRequestUseCase createRequestUseCase;

    @Mock
    private SubmitRequestUseCase submitRequestUseCase;

    @Mock
    private ApproveRequestUseCase approveRequestUseCase;

    @Mock
    private MedicalRequestWebMapper mapper;

    @InjectMocks
    private MedicalRequestController controller;

    private UUID beneficiaryId;
    private UUID requestId;
    private MedicalRequest medicalRequest;
    private MedicalRequestResponse medicalRequestResponse;

    @BeforeEach
    void setUp() {
        beneficiaryId = UUID.randomUUID();
        requestId = UUID.randomUUID();

        MedicalProcedure procedure = MedicalProcedure.create(
                UUID.randomUUID(),
                "ICD10-A00",
                "CBO-225125",
                "Test Procedure",
                1
        );
        procedure.markCoverageApproved();

        medicalRequest = MedicalRequest.reconstitute(
                requestId,
                beneficiaryId,
                "Test Request",
                RequestStatus.DRAFT,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                List.of(procedure)
        );

        medicalRequestResponse = MedicalRequestResponse.builder()
                .id(requestId)
                .beneficiaryId(beneficiaryId)
                .description("Test Request")
                .status(RequestStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void create_ShouldReturnCreatedStatus() {
        CreateRequestRequest request = new CreateRequestRequest();
        CreateRequestCommand command = CreateRequestCommand.builder().build();

        when(mapper.toCommand(request)).thenReturn(command);
        when(createRequestUseCase.create(command)).thenReturn(medicalRequest);
        when(mapper.toResponse(medicalRequest)).thenReturn(medicalRequestResponse);

        ResponseEntity<MedicalRequestResponse> response = controller.create(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(requestId);
        verify(createRequestUseCase).create(command);
        verify(mapper).toCommand(request);
        verify(mapper).toResponse(medicalRequest);
    }

    @Test
    void submit_ShouldReturnSubmittedRequest() {
        MedicalRequest submittedRequest = MedicalRequest.reconstitute(
                requestId,
                beneficiaryId,
                "Test Request",
                RequestStatus.SUBMITTED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of()
        );

        MedicalRequestResponse submittedResponse = MedicalRequestResponse.builder()
                .id(requestId)
                .status(RequestStatus.SUBMITTED)
                .build();

        when(submitRequestUseCase.submit(requestId)).thenReturn(submittedRequest);
        when(mapper.toResponse(submittedRequest)).thenReturn(submittedResponse);

        MedicalRequestResponse response = controller.submit(requestId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(requestId);
        assertThat(response.getStatus()).isEqualTo(RequestStatus.SUBMITTED);
        verify(submitRequestUseCase).submit(requestId);
        verify(mapper).toResponse(submittedRequest);
    }

    @Test
    void approve_WithApprovedTrue_ShouldReturnApprovedRequest() {
        MedicalRequest approvedRequest = MedicalRequest.reconstitute(
                requestId,
                beneficiaryId,
                "Test Request",
                RequestStatus.APPROVED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of()
        );

        MedicalRequestResponse approvedResponse = MedicalRequestResponse.builder()
                .id(requestId)
                .status(RequestStatus.APPROVED)
                .build();

        when(approveRequestUseCase.approve(requestId, true)).thenReturn(approvedRequest);
        when(mapper.toResponse(approvedRequest)).thenReturn(approvedResponse);

        MedicalRequestResponse response = controller.approve(requestId, true);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(requestId);
        assertThat(response.getStatus()).isEqualTo(RequestStatus.APPROVED);
        verify(approveRequestUseCase).approve(requestId, true);
        verify(mapper).toResponse(approvedRequest);
    }

    @Test
    void approve_WithApprovedFalse_ShouldReturnRejectedRequest() {
        MedicalRequest rejectedRequest = MedicalRequest.reconstitute(
                requestId,
                beneficiaryId,
                "Test Request",
                RequestStatus.REJECTED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of()
        );

        MedicalRequestResponse rejectedResponse = MedicalRequestResponse.builder()
                .id(requestId)
                .status(RequestStatus.REJECTED)
                .build();

        when(approveRequestUseCase.approve(requestId, false)).thenReturn(rejectedRequest);
        when(mapper.toResponse(rejectedRequest)).thenReturn(rejectedResponse);

        MedicalRequestResponse response = controller.approve(requestId, false);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(requestId);
        assertThat(response.getStatus()).isEqualTo(RequestStatus.REJECTED);
        verify(approveRequestUseCase).approve(requestId, false);
        verify(mapper).toResponse(rejectedRequest);
    }
}
