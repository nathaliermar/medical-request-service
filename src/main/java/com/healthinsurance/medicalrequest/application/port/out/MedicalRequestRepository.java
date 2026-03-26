package com.healthinsurance.medicalrequest.application.port.out;

import com.healthinsurance.medicalrequest.domain.model.MedicalRequest;
import com.healthinsurance.medicalrequest.domain.model.RequestStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for medical request persistence.
 */
public interface MedicalRequestRepository {

    MedicalRequest save(MedicalRequest request);

    Optional<MedicalRequest> findById(UUID id);

    List<MedicalRequest> findByBeneficiaryIdAndStatus(UUID beneficiaryId, RequestStatus status);

    List<MedicalRequest> findByBeneficiaryId(UUID beneficiaryId);

    List<MedicalRequest> findByStatus(RequestStatus status);

    List<MedicalRequest> findAll();
}
