package com.healthinsurance.medicalrequest.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CreateRequestCommand {
    private UUID beneficiaryId;
    private String description;
    private List<ProcedureData> procedures;

    @Getter
    @Builder
    public static class ProcedureData {
        private String icdCode;
        private String cboCode;
        private String description;
        private int quantity;
    }
}
