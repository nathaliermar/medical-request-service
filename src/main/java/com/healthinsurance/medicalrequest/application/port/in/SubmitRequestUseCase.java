package com.healthinsurance.medicalrequest.application.port.in;

import com.healthinsurance.medicalrequest.domain.model.MedicalRequest;

import java.util.UUID;

public interface SubmitRequestUseCase {
    MedicalRequest submit(UUID requestId);
}
