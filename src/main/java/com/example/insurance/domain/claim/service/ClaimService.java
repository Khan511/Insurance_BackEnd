package com.example.insurance.domain.claim.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.insurance.common.enummuration.ClaimStatus;
import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.global.config.CustomUserDetails;
import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.claim.ClaimSubmissionDTO;

public interface ClaimService {

    void submitClaim(ClaimSubmissionDTO claimSubmissionDTO, CustomUserDetails customUserDetails);

    List<ClaimResponseDTO> getAllClaimOfUser(String userId);

    Claim findByClaimNumber(String claimNumber);

    // // New methods for business operations
    // void approveClaim(Long claimId, String adminUsername, BigDecimal
    // approvedAmount);

    // void rejectClaim(Long claimId, String adminUsername, String rejectionReason);

    // void markClaimAsPaid(Long claimId, String adminUsername);

    // void updateClaimStatus(Long claimId, ClaimStatus newStatus, String
    // adminUsername, String reason);

    // // Query methods
    // Claim getClaimDetails(Long claimId);

    // List<Claim> getAllClaims();

    // List<Claim> getOpenClaims();

    // List<Claim> getProcessedClaims();

    // long getAverageProcessingTime();

    // // Old method (deprecated)
    // @Deprecated
    // void processClaim(Long claimId, BigDecimal amount, ClaimStatus status);

}
