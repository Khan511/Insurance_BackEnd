package com.example.insurance.domain.claim.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ClaimAssessment {

    @Column(name = "claimed_amount")
    private BigDecimal claimedAmount;

    @Column(name = "approved_amount")
    private BigDecimal approvedAmount;

    @Column(name = "denial_reason_code")
    private String denialReasonCode;

    @Column(name = "assessment_date")
    private LocalDateTime assessmentDate;

    @Column(name = "assessor_id")
    private String assessorId;

    @Column(name = "fraud_flag")
    private boolean fraudFlag;

}
