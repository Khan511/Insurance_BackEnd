package com.example.insurance.common.enummuration;

public enum UserStatus {

    /**
     * Account created but email/phone not verified
     * - Can't purchase policies
     * - Can't file claims
     */
    PENDING_VERIFICATION,

    /**
     * Fully active account
     * - Full system access
     * - Can purchase policies
     * - Can file claims
     */
    ACTIVE,

    /**
     * Temporary suspension (fraud investigation, compliance issues)
     * - Can't purchase new policies
     * - Existing policies remain active
     * - Claims processing paused
     */
    SUSPENDED,

    /**
     * User-initiated deactivation
     * - No new policy purchases
     * - Existing policies continue until expiration
     * - Read-only access to documents
     */
    DEACTIVATED,

    /**
     * Compliance/security initiated permanent block
     * - All active policies cancelled
     * - Claims processing stopped
     * - GDPR-compliant data retention
     */
    BLACKLISTED,

    /**
     * System-managed status for GDPR right-to-be-forgotten
     * - All PII data pseudonymized
     * - Financial records retained for compliance
     */
    ANONYMIZED

}
