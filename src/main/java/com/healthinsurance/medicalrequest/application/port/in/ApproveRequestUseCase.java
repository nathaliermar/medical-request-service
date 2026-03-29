package com.healthinsurance.medicalrequest.application.port.in;

import com.healthinsurance.medicalrequest.domain.model.MedicalRequest;

import java.util.UUID;

public interface ApproveRequestUseCase {
    MedicalRequest approve(UUID requestId, boolean approved);
}
