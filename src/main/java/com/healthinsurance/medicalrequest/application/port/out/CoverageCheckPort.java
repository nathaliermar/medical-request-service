package com.healthinsurance.medicalrequest.application.port.out;

/**
 * Port for external coverage validation service.
 */
public interface CoverageCheckPort {

    boolean isCovered(String beneficiaryId, String icdCode, String cboCode);
}
