package com.example.insurance.domain.claim.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class ClaimIdGenerator {

    public String generateTemporaryClaimId() {
        // Old code:
        // public String generateUniqueClaimId() {
        //     String claimId;
        //     boolean isUnique;
        //
        //     do {
        //         Random random = new Random();
        //         int num = random.nextInt(900000) + 100000;
        //         claimId = "CLM-" + String.valueOf(num);
        //         isUnique = !claimRepository.existsByClaimNumber(claimId);
        //     } while (!isUnique);
        //
        //     return claimId;
        // }
        return "TMP-" + UUID.randomUUID();
    }

    public String generateClaimId(Long claimDatabaseId) {
        return String.format("CLM-%06d", claimDatabaseId);
    }
}
