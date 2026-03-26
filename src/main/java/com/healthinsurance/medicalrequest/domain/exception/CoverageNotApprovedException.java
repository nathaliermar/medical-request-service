package com.healthinsurance.medicalrequest.domain.exception;

public class CoverageNotApprovedException extends DomainException {
    public CoverageNotApprovedException(String icdCode) {
        super("Coverage check failed for procedure with ICD code: " + icdCode);
    }
}
