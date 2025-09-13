package com.example.insurance.common.enummuration;

public enum ClaimStatus {

    // Initial state after claim submission
    PENDING,

    // Claim is being reviewed by adjusters
    UNDER_REVIEW,

    // Additional information requested
    AWAITING_DOCUMENTATION,

    // Expert evaluation needed
    REQUIRES_EXPERT_ASSESSMENT,

    // Fraud detection triggered
    FRAUD_INVESTIGATION,

    // Approved for payment
    APPROVED,

    // Payment processed
    PAYMENT_PROCESSED,

    // Claim rejected
    DENIED,

    // Claim withdrawn by customer
    WITHDRAWN,

    // Post-payment closure
    CLOSED,

    // Reopened for reevaluation
    REOPENED;

    // Optional: Business logic methods
    public boolean isActive() {
        return this != CLOSED && this != DENIED && this != WITHDRAWN;
    }

    public boolean requiresAttention() {
        return this == AWAITING_DOCUMENTATION || this == REQUIRES_EXPERT_ASSESSMENT;

    }
}
