package com.healthinsurance.medicalrequest.domain.exception;

public class InvalidStatusTransitionException extends DomainException {
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
