package com.healthinsurance.medicalrequest.infrastructure.soap;

import com.healthinsurance.medicalrequest.application.port.out.CoverageCheckPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Mock SOAP client for coverage validation.
 * Replace with real implementation using Spring profiles.
 */
@Slf4j
@Component
public class MockCoverageCheckSoapClient implements CoverageCheckPort {

    private static final Set<String> EXCLUDED_ICD_CODES = Set.of("Z00.0", "Z41.1", "COSMETIC");

    @Override
    public boolean isCovered(String beneficiaryId, String icdCode, String cboCode) {
        log.info("[MOCK SOAP] Coverage check — beneficiaryId={}, icd={}, cbo={}", beneficiaryId, icdCode, cboCode);

        boolean covered = !EXCLUDED_ICD_CODES.contains(icdCode);

        log.info("[MOCK SOAP] Coverage result={} for icd={}", covered, icdCode);
        return covered;
    }
}
