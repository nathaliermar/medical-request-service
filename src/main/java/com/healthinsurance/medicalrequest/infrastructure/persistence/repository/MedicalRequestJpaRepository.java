package com.healthinsurance.medicalrequest.infrastructure.persistence.repository;

import com.healthinsurance.medicalrequest.domain.model.RequestStatus;
import com.healthinsurance.medicalrequest.infrastructure.persistence.entity.MedicalRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicalRequestJpaRepository extends JpaRepository<MedicalRequestEntity, UUID> {

    List<MedicalRequestEntity> findByBeneficiaryId(UUID beneficiaryId);

    List<MedicalRequestEntity> findByStatus(RequestStatus status);

    List<MedicalRequestEntity> findByBeneficiaryIdAndStatus(UUID beneficiaryId, RequestStatus status);
}
