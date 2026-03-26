package com.healthinsurance.medicalrequest.domain.model;

import com.healthinsurance.medicalrequest.domain.exception.DomainException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate root for medical procedure requests.
 * Domain entity with no JPA dependencies - mapped via infrastructure layer.
 */
@Getter
public class MedicalRequest {

    private UUID id;
    private UUID beneficiaryId;
    private String description;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime submittedAt;

    private List<MedicalProcedure> procedures;

    private MedicalRequest() {}

    public static MedicalRequest create(UUID beneficiaryId, String description,
                                        List<MedicalProcedure> procedures) {
        if (procedures == null || procedures.isEmpty()) {
            throw new DomainException("A medical request must contain at least one procedure");
        }
        MedicalRequest request = new MedicalRequest();
        request.id = UUID.randomUUID();
        request.beneficiaryId = beneficiaryId;
        request.description = description;
        request.status = RequestStatus.DRAFT;
        request.createdAt = LocalDateTime.now();
        request.updatedAt = LocalDateTime.now();
        request.procedures = new ArrayList<>(procedures);
        return request;
    }

    public static MedicalRequest reconstitute(UUID id, UUID beneficiaryId, String description,
                                               RequestStatus status, LocalDateTime createdAt,
                                               LocalDateTime updatedAt, LocalDateTime submittedAt,
                                               List<MedicalProcedure> procedures) {
        MedicalRequest request = new MedicalRequest();
        request.id = id;
        request.beneficiaryId = beneficiaryId;
        request.description = description;
        request.status = status;
        request.createdAt = createdAt;
        request.updatedAt = updatedAt;
        request.submittedAt = submittedAt;
        request.procedures = procedures != null ? new ArrayList<>(procedures) : new ArrayList<>();
        return request;
    }

    public void transitionTo(RequestStatus newStatus) {
        this.status = this.status.transitionTo(newStatus);
        this.updatedAt = LocalDateTime.now();
        if (newStatus == RequestStatus.SUBMITTED) {
            this.submittedAt = LocalDateTime.now();
        }
    }


    public List<MedicalProcedure> getProcedures() {
        return Collections.unmodifiableList(procedures);
    }
}
