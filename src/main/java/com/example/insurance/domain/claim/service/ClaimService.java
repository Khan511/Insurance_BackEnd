package com.example.insurance.domain.claim.service;

import java.util.List;
import com.example.insurance.global.config.CustomUserDetails;
import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.claim.ClaimSubmissionDTO;

public interface ClaimService {

    public void submitClaim(ClaimSubmissionDTO claimSubmissionDTO, CustomUserDetails customUserDetails);

    List<ClaimResponseDTO> getAllClaimOfUser(String userId);

    



}
