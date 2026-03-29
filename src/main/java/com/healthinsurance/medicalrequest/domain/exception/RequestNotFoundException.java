package com.healthinsurance.medicalrequest.domain.exception;

import java.util.UUID;

public class RequestNotFoundException extends DomainException {
    public RequestNotFoundException(UUID id) {
        super("Medical request not found with id: " + id);
    }
}
