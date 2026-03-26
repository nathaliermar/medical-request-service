package com.healthinsurance.medicalrequest.infrastructure.web.mapper;

import com.healthinsurance.medicalrequest.application.dto.*;
import com.healthinsurance.medicalrequest.domain.model.*;
import com.healthinsurance.medicalrequest.infrastructure.web.dto.request.*;
import com.healthinsurance.medicalrequest.infrastructure.web.dto.response.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicalRequestWebMapper {

    default CreateRequestCommand toCommand(CreateRequestRequest request) {
        List<CreateRequestCommand.ProcedureData> procedures = request.getProcedures().stream()
                .map(p -> CreateRequestCommand.ProcedureData.builder()
                                .icdCode(p.getIcdCode())
                                .cboCode(p.getCboCode())
                                .description(p.getDescription())
                                .quantity(p.getQuantity())
                                .build())
                        .toList();

        return CreateRequestCommand.builder()
                .beneficiaryId(request.getBeneficiaryId())
                .description(request.getDescription())
                .procedures(procedures)
                .build();
    }



    default MedicalRequestResponse toResponse(MedicalRequest domain) {
        return MedicalRequestResponse.builder()
                .id(domain.getId())
                .beneficiaryId(domain.getBeneficiaryId())
                .description(domain.getDescription())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .submittedAt(domain.getSubmittedAt())
                .procedures(domain.getProcedures().stream().map(this::toProcedureResponse).toList())
                .build();
    }

    default MedicalRequestResponse.ProcedureResponse toProcedureResponse(MedicalProcedure p) {
    return MedicalRequestResponse.ProcedureResponse.builder()
            .id(p.getId())
            .icdCode(p.getIcdCode())
            .cboCode(p.getCboCode())
            .description(p.getDescription())
            .quantity(p.getQuantity())
            .coverageApproved(p.isCoverageApproved())
            .build();
    }

    List<MedicalRequestResponse> toResponseList(List<MedicalRequest> domains);
}
