package com.healthinsurance.medicalrequest.infrastructure.web.dto.response;

import com.healthinsurance.medicalrequest.domain.model.RequestStatus;
import com.healthinsurance.medicalrequest.domain.model.HospitalizationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MedicalRequestResponse {

    private UUID id;
    private UUID beneficiaryId;
    private String description;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime submittedAt;

    private List<ProcedureResponse> procedures;
    private List<AttachmentResponse> attachments;
    private List<PendingItemResponse> pendingItems;
    private HospitalizationResponse hospitalization;
    private AnalysisResponse analysis;

    @Data
    @Builder
    public static class ProcedureResponse {
        private UUID id;
        private String icdCode;
        private String cboCode;
        private String description;
        private int quantity;
        private boolean coverageApproved;
    }

    @Data
    @Builder
    public static class HospitalizationResponse {
        private UUID id;
        private LocalDateTime expectedAdmissionDate;
        private LocalDateTime expectedDischargeDate;
        private String hospitalName;
        private String hospitalCnesCode;
        private String clinicalIndication;
        private HospitalizationType type;
    }

    @Data
    @Builder
    public static class AnalysisResponse {
        private UUID id;
        private UUID reviewerId;
        private String justification;
        private String outcome;
        private LocalDateTime analyzedAt;
    }
}
