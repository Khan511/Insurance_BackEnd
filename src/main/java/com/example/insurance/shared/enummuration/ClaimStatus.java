
package com.example.insurance.shared.enummuration;

public enum ClaimStatus {
    PENDING,
    UNDER_REVIEW,
    PAUSED,
    APPROVED,
    PAID,
    REJECTED,
    WITHDRAWN,
    CLOSED,
    CANCELLED,
    EXPIRED;

    // Optional: Business logic methods
    public boolean isActive() {
        return this != CLOSED && this != REJECTED && this != WITHDRAWN && this != CANCELLED && this != PAID;
    }

    public boolean isTerminal() {
        return this == REJECTED || this == PAID || this == CLOSED || this == WITHDRAWN || this == CANCELLED;
    }

    // Helper to check if status change requires approval amount
    public boolean requiresAmount() {
        return this == APPROVED;
    }

    // Helper to check if status change requires reason
    public boolean requiresReason() {
        return this == REJECTED || this == CANCELLED;
    }
}