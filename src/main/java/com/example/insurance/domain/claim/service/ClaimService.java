package com.example.insurance.domain.claim.service;

import com.example.insurance.infrastructure.web.claim.ClaimSubmissionDTO;

public interface ClaimService {

    public void submitClaim(ClaimSubmissionDTO claimSubmissionDTO);

}
