package com.healthinsurance.medicalrequest.infrastructure.web.controller;

import com.healthinsurance.medicalrequest.application.port.in.*;
import com.healthinsurance.medicalrequest.infrastructure.web.dto.request.*;
import com.healthinsurance.medicalrequest.infrastructure.web.dto.response.*;
import com.healthinsurance.medicalrequest.infrastructure.web.mapper.MedicalRequestWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Tag(name = "Medical Requests", description = "Manage medical procedure authorisation requests")
@SecurityRequirement(name = "bearerAuth")
public class MedicalRequestController {

    private final CreateRequestUseCase createRequestUseCase;
    private final SubmitRequestUseCase submitRequestUseCase;
    private final ApproveRequestUseCase approveRequestUseCase;
    private final MedicalRequestWebMapper mapper;

    @PostMapping
    @Operation(summary = "Create a new medical request (DRAFT)")
    @ApiResponse(responseCode = "201", description = "Request created")
    @ApiResponse(responseCode = "422", description = "Coverage not approved for a procedure")
    public ResponseEntity<MedicalRequestResponse> create(
            @Valid @RequestBody CreateRequestRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(createRequestUseCase.create(mapper.toCommand(body))));
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit a DRAFT request for approval")
    @ApiResponse(responseCode = "200", description = "Request submitted")
    @ApiResponse(responseCode = "422", description = "Invalid status transition")
    public MedicalRequestResponse submit(@PathVariable java.util.UUID id) {
        return mapper.toResponse(submitRequestUseCase.submit(id));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve or reject a SUBMITTED request")
    @ApiResponse(responseCode = "200", description = "Request approved or rejected")
    @ApiResponse(responseCode = "422", description = "Invalid status transition")
    public MedicalRequestResponse approve(
            @PathVariable java.util.UUID id,
            @RequestParam boolean approved) {
        return mapper.toResponse(approveRequestUseCase.approve(id, approved));
    }
}
