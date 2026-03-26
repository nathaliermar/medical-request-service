package com.healthinsurance.medicalrequest.domain.model;

import com.healthinsurance.medicalrequest.domain.exception.InvalidStatusTransitionException;

import java.util.Set;
import java.util.Map;

/**
 * Request lifecycle states with enforced transitions.
 */
public enum RequestStatus {

    DRAFT,
    SUBMITTED,
    APPROVED,
    REJECTED,
    CANCELLED;

    private static final Map<RequestStatus, Set<RequestStatus>> ALLOWED_TRANSITIONS = Map.of(
            DRAFT,       Set.of(SUBMITTED, CANCELLED),
            SUBMITTED,   Set.of(APPROVED, REJECTED, CANCELLED),
            APPROVED,    Set.of(),
            REJECTED,    Set.of(),
            CANCELLED,   Set.of()
    );

    public RequestStatus transitionTo(RequestStatus target) {
        Set<RequestStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(this, Set.of());
        if (!allowed.contains(target)) {
            throw new InvalidStatusTransitionException(
                    String.format("Transition from %s to %s is not allowed", this, target));
        }
        return target;
    }

    public boolean isTerminal() {
        return this == APPROVED || this == REJECTED || this == CANCELLED;
    }
}
