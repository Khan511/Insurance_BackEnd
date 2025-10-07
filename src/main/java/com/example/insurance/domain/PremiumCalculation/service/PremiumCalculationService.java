package com.example.insurance.domain.PremiumCalculation.service;

import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.infrastructure.web.premiumCalculation.PremiumCalculationRequest;
import com.example.insurance.infrastructure.web.premiumCalculation.PremiumCalculationResponse;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public interface PremiumCalculationService {
    PremiumCalculationResponse calculatePremium(PremiumCalculationRequest request, String userEmail);

    int calculateAge(LocalDate birthDate);
    // Remove or keep the old calculatePremium method based on your needs
}

// @Service
// public interface PremiumCalculationService {

// public MonetaryAmount calculatePremium(InsuranceProduct product, Map<String,
// Object> riskFactors);

// public int calculateAge(LocalDate birthDate);

// }