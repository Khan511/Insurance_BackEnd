package com.example.insurance.usecases.admin.controller;

import java.math.BigDecimal;
import com.example.insurance.common.enummuration.ClaimStatus;
import com.example.insurance.infrastructure.web.claim.IncidentDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminClaimUpdateRequest {

    private String policyNumber;

    private String claimNumber;
    private String claimId;

    private ClaimStatus status;
    private BigDecimal amount;

    private boolean thirdPartyInvolved;

    private String claimType;

    private IncidentDetailsDTO incidentDetails;

}
