package com.healthinsurance.medicalrequest.infrastructure.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateRequestRequest {

    @NotNull(message = "beneficiaryId is required")
    private UUID beneficiaryId;

    @NotBlank(message = "description is required")
    @Size(max = 1000, message = "description must be at most 1000 characters")
    private String description;

    @NotEmpty(message = "At least one procedure is required")
    @Valid
    private List<ProcedureRequest> procedures;

    @Data
    public static class ProcedureRequest {

        @NotBlank(message = "icdCode is required")
        @Size(max = 20, message = "icdCode must be at most 20 characters")
        private String icdCode;

        @Size(max = 20, message = "cboCode must be at most 20 characters")
        private String cboCode;

        @Size(max = 500, message = "description must be at most 500 characters")
        private String description;

        @Min(value = 1, message = "quantity must be at least 1")
        @Max(value = 999, message = "quantity must be at most 999")
        private int quantity = 1;
    }
}
