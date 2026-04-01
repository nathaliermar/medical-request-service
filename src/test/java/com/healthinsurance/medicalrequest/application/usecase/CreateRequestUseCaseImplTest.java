package com.healthinsurance.medicalrequest.application.usecase;

import com.healthinsurance.medicalrequest.application.dto.CreateRequestCommand;
import com.healthinsurance.medicalrequest.application.port.out.CoverageCheckPort;
import com.healthinsurance.medicalrequest.application.port.out.MedicalRequestRepository;
import com.healthinsurance.medicalrequest.domain.exception.CoverageNotApprovedException;
import com.healthinsurance.medicalrequest.domain.model.MedicalRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRequestUseCaseImplTest {

    @Mock
    private MedicalRequestRepository repository;

    @Mock
    private CoverageCheckPort coverageCheckPort;

    @InjectMocks
    private CreateRequestUseCaseImpl useCase;

    private UUID beneficiaryId;
    private CreateRequestCommand command;

    @BeforeEach
    void setUp() {
        beneficiaryId = UUID.randomUUID();
        
        CreateRequestCommand.ProcedureData procedure = CreateRequestCommand.ProcedureData.builder()
                .icdCode("ICD10-A00")
                .cboCode("CBO-225125")
                .description("Test Procedure")
                .quantity(1)
                .build();

        command = CreateRequestCommand.builder()
                .beneficiaryId(beneficiaryId)
                .description("Test Request")
                .procedures(List.of(procedure))
                .build();
    }

    @Test
    void create_WithCoveredProcedure_ShouldCreateRequest() {
        when(coverageCheckPort.isCovered(anyString(), anyString(), anyString())).thenReturn(true);
        when(repository.save(any(MedicalRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MedicalRequest result = useCase.create(command);

        assertThat(result).isNotNull();
        assertThat(result.getBeneficiaryId()).isEqualTo(beneficiaryId);
        assertThat(result.getDescription()).isEqualTo("Test Request");
        assertThat(result.getProcedures()).hasSize(1);
        assertThat(result.getProcedures().get(0).isCoverageApproved()).isTrue();
        
        verify(coverageCheckPort).isCovered(
                beneficiaryId.toString(),
                "ICD10-A00",
                "CBO-225125"
        );
        verify(repository).save(any(MedicalRequest.class));
    }

    @Test
    void create_WithNotCoveredProcedure_ShouldThrowException() {
        when(coverageCheckPort.isCovered(anyString(), anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> useCase.create(command))
                .isInstanceOf(CoverageNotApprovedException.class);

        verify(coverageCheckPort).isCovered(
                beneficiaryId.toString(),
                "ICD10-A00",
                "CBO-225125"
        );
        verify(repository, never()).save(any(MedicalRequest.class));
    }

    @Test
    void create_WithMultipleProcedures_ShouldValidateAllCoverage() {
        CreateRequestCommand.ProcedureData procedure1 = CreateRequestCommand.ProcedureData.builder()
                .icdCode("ICD10-A00")
                .cboCode("CBO-225125")
                .description("Procedure 1")
                .quantity(1)
                .build();

        CreateRequestCommand.ProcedureData procedure2 = CreateRequestCommand.ProcedureData.builder()
                .icdCode("ICD10-B00")
                .cboCode("CBO-225126")
                .description("Procedure 2")
                .quantity(2)
                .build();

        CreateRequestCommand multiProcCommand = CreateRequestCommand.builder()
                .beneficiaryId(beneficiaryId)
                .description("Multi Procedure Request")
                .procedures(List.of(procedure1, procedure2))
                .build();

        when(coverageCheckPort.isCovered(anyString(), anyString(), anyString())).thenReturn(true);
        when(repository.save(any(MedicalRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MedicalRequest result = useCase.create(multiProcCommand);

        assertThat(result).isNotNull();
        assertThat(result.getProcedures()).hasSize(2);
        assertThat(result.getProcedures().get(0).isCoverageApproved()).isTrue();
        assertThat(result.getProcedures().get(1).isCoverageApproved()).isTrue();
        
        verify(coverageCheckPort, times(2)).isCovered(anyString(), anyString(), anyString());
        verify(repository).save(any(MedicalRequest.class));
    }

    @Test
    void create_WithSecondProcedureNotCovered_ShouldThrowException() {
        CreateRequestCommand.ProcedureData procedure1 = CreateRequestCommand.ProcedureData.builder()
                .icdCode("ICD10-A00")
                .cboCode("CBO-225125")
                .description("Procedure 1")
                .quantity(1)
                .build();

        CreateRequestCommand.ProcedureData procedure2 = CreateRequestCommand.ProcedureData.builder()
                .icdCode("ICD10-B00")
                .cboCode("CBO-225126")
                .description("Procedure 2")
                .quantity(2)
                .build();

        CreateRequestCommand multiProcCommand = CreateRequestCommand.builder()
                .beneficiaryId(beneficiaryId)
                .description("Multi Procedure Request")
                .procedures(List.of(procedure1, procedure2))
                .build();

        when(coverageCheckPort.isCovered(anyString(), eq("ICD10-A00"), anyString())).thenReturn(true);
        when(coverageCheckPort.isCovered(anyString(), eq("ICD10-B00"), anyString())).thenReturn(false);

        assertThatThrownBy(() -> useCase.create(multiProcCommand))
                .isInstanceOf(CoverageNotApprovedException.class);

        verify(repository, never()).save(any(MedicalRequest.class));
    }
}
