package com.example.insurance.domain.PremiumCalculation.service;

import com.example.insurance.infrastructure.web.premiumCalculation.PremiumCalculationRequest;
import com.example.insurance.infrastructure.web.premiumCalculation.PremiumCalculationResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface PremiumCalculationService {
    PremiumCalculationResponse calculatePremium(PremiumCalculationRequest request, String userEmail);

    int calculateAge(LocalDate birthDate);
}
