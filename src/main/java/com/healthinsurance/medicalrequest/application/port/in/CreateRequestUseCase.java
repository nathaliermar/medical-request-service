package com.healthinsurance.medicalrequest.application.port.in;

import com.healthinsurance.medicalrequest.application.dto.CreateRequestCommand;
import com.healthinsurance.medicalrequest.domain.model.MedicalRequest;

public interface CreateRequestUseCase {
    MedicalRequest create(CreateRequestCommand command);
}
