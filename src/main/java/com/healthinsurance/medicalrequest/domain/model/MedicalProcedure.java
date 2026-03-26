package com.healthinsurance.medicalrequest.domain.model;

import lombok.Getter;

import java.util.UUID;

/**
 * Medical procedure entity within a request.
 * Mutable entity with coverage validation state.
 */
@Getter
public class MedicalProcedure {

    private final UUID id;
    private final String icdCode;
    private final String cboCode;
    private final String description;
    private final int quantity;
    private boolean coverageApproved; // mutable — set after external coverage check

    private MedicalProcedure(UUID id, String icdCode, String cboCode,
                              String description, int quantity, boolean coverageApproved) {
        this.id = id;
        this.icdCode = icdCode;
        this.cboCode = cboCode;
        this.description = description;
        this.quantity = quantity;
        this.coverageApproved = coverageApproved;
    }

    public static MedicalProcedure create(UUID id, String icdCode, String cboCode,
                                          String description, int quantity) {
        return new MedicalProcedure(id, icdCode, cboCode, description, quantity, false);
    }

    public static MedicalProcedure reconstitute(UUID id, String icdCode, String cboCode,
                                                String description, int quantity,
                                                boolean coverageApproved) {
        return new MedicalProcedure(id, icdCode, cboCode, description, quantity, coverageApproved);
    }

    public void markCoverageApproved() {
        this.coverageApproved = true;
    }

    public void markCoverageRejected() {
        this.coverageApproved = false;
    }
}
