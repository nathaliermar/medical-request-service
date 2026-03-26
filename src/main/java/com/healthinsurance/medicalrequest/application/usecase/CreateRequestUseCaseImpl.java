package com.healthinsurance.medicalrequest.application.usecase;

import com.healthinsurance.medicalrequest.application.dto.CreateRequestCommand;
import com.healthinsurance.medicalrequest.application.port.in.CreateRequestUseCase;
import com.healthinsurance.medicalrequest.application.port.out.CoverageCheckPort;
import com.healthinsurance.medicalrequest.application.port.out.MedicalRequestRepository;
import com.healthinsurance.medicalrequest.domain.exception.CoverageNotApprovedException;
import com.healthinsurance.medicalrequest.domain.model.MedicalProcedure;
import com.healthinsurance.medicalrequest.domain.model.MedicalRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

/**
 * Creates medical requests with coverage validation.
 */
@Slf4j
@RequiredArgsConstructor
public class CreateRequestUseCaseImpl implements CreateRequestUseCase {

    private final MedicalRequestRepository repository;
    private final CoverageCheckPort coverageCheckPort;

    @Override
    public MedicalRequest create(CreateRequestCommand command) {
        log.info("Creating medical request for beneficiary={}", command.getBeneficiaryId());

        List<MedicalProcedure> procedures = command.getProcedures().stream()
                .map(p -> MedicalProcedure.create(
                        UUID.randomUUID(),
                        p.getIcdCode(),
                        p.getCboCode(),
                        p.getDescription(),
                        p.getQuantity()))
                .toList();

        for (MedicalProcedure procedure : procedures) {
            boolean covered = coverageCheckPort.isCovered(
                    command.getBeneficiaryId().toString(),
                    procedure.getIcdCode(),
                    procedure.getCboCode()
            );
            if (covered) {
                procedure.markCoverageApproved();
            } else {
                throw new CoverageNotApprovedException(procedure.getIcdCode());
            }
        }

        MedicalRequest request = MedicalRequest.create(
                command.getBeneficiaryId(),
                command.getDescription(),
                procedures
                );

        MedicalRequest saved = repository.save(request);
        log.info("Medical request created id={}", saved.getId());
        return saved;
    }
}
