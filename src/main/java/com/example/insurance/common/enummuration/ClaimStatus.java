// package com.example.insurance.common.enummuration;

// public enum ClaimStatus {

//     // Initial state after claim submission
//     PENDING,

//     // Claim is being reviewed by adjusters
//     UNDER_REVIEW,

//     UNDER_INVESTIGATION,

//     // Approved for payment
//     APPROVED,

//     PAID,

//     // Claim rejected
//     REJECTED,

//     // Claim withdrawn by customer
//     WITHDRAWN,

//     // Post-payment closure
//     CLOSED,

//     CANCELLED;

//     // Optional: Business logic methods
//     public boolean isActive() {
//         return this != CLOSED && this != REJECTED && this != WITHDRAWN;
//     }

//     public boolean isTerminal() {
//         return this == REJECTED || this == PAID || this == CLOSED;
//     }

// }

package com.example.insurance.common.enummuration;

public enum ClaimStatus {
    PENDING,
    UNDER_REVIEW,
    UNDER_INVESTIGATION,
    APPROVED,
    PAID,
    REJECTED,
    WITHDRAWN,
    CLOSED,
    CANCELLED;

    // Optional: Business logic methods
    public boolean isActive() {
        return this != CLOSED && this != REJECTED && this != WITHDRAWN && this != CANCELLED;
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