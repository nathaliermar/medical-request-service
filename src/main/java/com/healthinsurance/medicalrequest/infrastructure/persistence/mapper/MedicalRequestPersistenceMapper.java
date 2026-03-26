package com.healthinsurance.medicalrequest.infrastructure.persistence.mapper;

import com.healthinsurance.medicalrequest.domain.model.*;
import com.healthinsurance.medicalrequest.infrastructure.persistence.entity.*;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper between domain models and JPA entities.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MedicalRequestPersistenceMapper {


    @Mapping(target = "procedures", ignore = true)
    MedicalRequestEntity toEntity(MedicalRequest domain);

    @Mapping(target = "request", ignore = true)
    MedicalProcedureEntity toProcedureEntity(MedicalProcedure domain);


    default MedicalRequest toDomain(MedicalRequestEntity entity) {
        if (entity == null) return null;

        List<MedicalProcedure> procedures = entity.getProcedures() == null ? List.of()
                : entity.getProcedures().stream().map(this::toProcedureDomain).toList();

        return MedicalRequest.reconstitute(
                entity.getId(), entity.getBeneficiaryId(), entity.getDescription(),
                entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt(),
                entity.getSubmittedAt(), procedures
        );
    }

    default MedicalProcedure toProcedureDomain(MedicalProcedureEntity e) {
        if (e == null) return null;
        return MedicalProcedure.reconstitute(
                e.getId(), e.getIcdCode(), e.getCboCode(),
                e.getDescription(), e.getQuantity(), e.isCoverageApproved());
    }
}
