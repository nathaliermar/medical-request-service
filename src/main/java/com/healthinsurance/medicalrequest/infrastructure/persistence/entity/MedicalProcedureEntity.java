package com.healthinsurance.medicalrequest.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "medical_procedures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalProcedureEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private MedicalRequestEntity request;

    @Column(name = "icd_code", nullable = false, length = 20)
    private String icdCode;

    @Column(name = "cbo_code", length = 20)
    private String cboCode;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "coverage_approved")
    private boolean coverageApproved;
}
