package com.example.insurance.domain.claim.service;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.example.insurance.domain.claim.repository.ClaimRepository;

@Component
@RequiredArgsConstructor
public class ClaimIdGenerator {

    private final ClaimRepository claimRepository;

    public String generateUniqueClaimId() {
        String claimId;
        boolean isUnique;

        do {
            // Generate a random 6-digit number
            Random random = new Random();
            int num = random.nextInt(900000) + 100000;
            claimId = "CLM-" + String.valueOf(num);

            // Check if ID already exists
            isUnique = !claimRepository.existsByClaimNumber(claimId);
        } while (!isUnique);

        return claimId;
    }
}
