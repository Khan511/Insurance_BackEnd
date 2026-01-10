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

}
