package com.healthinsurance.medicalrequest.infrastructure.web.dto.response;

import com.healthinsurance.medicalrequest.domain.model.RequestStatus;
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
}
