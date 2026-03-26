package com.healthinsurance.medicalrequest.infrastructure.persistence.adapter;

import com.healthinsurance.medicalrequest.application.port.out.MedicalRequestRepository;
import com.healthinsurance.medicalrequest.domain.model.MedicalRequest;
import com.healthinsurance.medicalrequest.domain.model.RequestStatus;
import com.healthinsurance.medicalrequest.infrastructure.persistence.entity.*;
import com.healthinsurance.medicalrequest.infrastructure.persistence.mapper.MedicalRequestPersistenceMapper;
import com.healthinsurance.medicalrequest.infrastructure.persistence.repository.MedicalRequestJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA adapter for MedicalRequestRepository.
 */
@Component

@RequiredArgsConstructor
public class MedicalRequestRepositoryAdapter implements MedicalRequestRepository {

    private final MedicalRequestJpaRepository jpaRepository;
    private final MedicalRequestPersistenceMapper mapper;

    @Override
    @Transactional
    public MedicalRequest save(MedicalRequest domain) {
        if (domain.getId() != null && jpaRepository.existsById(domain.getId())) {
            MedicalRequestEntity entity = jpaRepository.findById(domain.getId())
                    .orElseThrow(() -> new IllegalStateException("Entity not found for update: " + domain.getId()));
            
            entity.setStatus(domain.getStatus());
            entity.setUpdatedAt(domain.getUpdatedAt());
            entity.setSubmittedAt(domain.getSubmittedAt());
            entity.setDescription(domain.getDescription());
            
            return mapper.toDomain(entity);
        } else {
            MedicalRequestEntity entity = toEntityWithRelations(domain);
            MedicalRequestEntity saved = jpaRepository.saveAndFlush(entity);
            return mapper.toDomain(saved);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicalRequest> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRequest> findByBeneficiaryIdAndStatus(UUID beneficiaryId, RequestStatus status) {
        return jpaRepository.findByBeneficiaryIdAndStatus(beneficiaryId, status)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRequest> findByBeneficiaryId(UUID beneficiaryId) {
        return jpaRepository.findByBeneficiaryId(beneficiaryId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRequest> findByStatus(RequestStatus status) {
        return jpaRepository.findByStatus(status)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRequest> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    private MedicalRequestEntity toEntityWithRelations(MedicalRequest domain) {
        MedicalRequestEntity entity = mapper.toEntity(domain);

        List<MedicalProcedureEntity> procedures = domain.getProcedures().stream()
                .map(p -> { var pe = mapper.toProcedureEntity(p); pe.setRequest(entity); return pe; })
                .toList();
        entity.getProcedures().clear();
        entity.getProcedures().addAll(procedures);

        return entity;
    }
}
