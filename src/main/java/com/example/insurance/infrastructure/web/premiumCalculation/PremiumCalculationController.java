package com.example.insurance.infrastructure.web.premiumCalculation;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.insurance.domain.PremiumCalculation.service.PremiumCalculationService;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.repository.InsuranceProductRepository;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.repository.UserRepository;
import com.example.insurance.global.config.CustomUserDetails;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/premium")
public class PremiumCalculationController {
    private final PremiumCalculationService premiumCalculationService;
    private final InsuranceProductRepository insuranceProductRepository;
    private final UserRepository userRepository; // Add this

    @PostMapping("/calculate")
    public PremiumCalculationResponse calculatePremium(
            @RequestBody PremiumCalculationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Get the current user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        InsuranceProduct product = insuranceProductRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Calculate age from user's date of birth
        int userAge = premiumCalculationService.calculateAge(user.getDateOfBirth());

        // Add the calculated age to risk factors
        request.getRiskFactors().put("age", userAge);

        // For Auto insurance, also add driverAge
        if (product.getProductType().name().equals("AUTO")) {
            request.getRiskFactors().put("driverAge", userAge);
        }
        // For Life insurance, also add insuredAge
        else if (product.getProductType().name().equals("LIFE")) {
            request.getRiskFactors().put("insuredAge", userAge);
        }

        System.out.println("User age calculated: " + userAge);
        System.out.println("Updated risk factors with age: " + request.getRiskFactors());

        MonetaryAmount calculatePremium = premiumCalculationService.calculatePremium(product, request.getRiskFactors());

        // calculate installment amount based on payment frequency
        BigDecimal installmentAmount = calculateInstallmentAmount(calculatePremium.getAmount(),
                request.getPaymentFrequency());

        return new PremiumCalculationResponse(calculatePremium.getAmount(),
                calculatePremium.getCurrency(), installmentAmount, request.getPaymentFrequency(),
                product.getCalculationConfig().getFormula());
    }

    private BigDecimal calculateInstallmentAmount(BigDecimal totalAnnualAmmount, String paymentFrequency) {
        if (paymentFrequency == null) {
            return totalAnnualAmmount;
        }

        switch (paymentFrequency.toUpperCase()) {
            case "MONTHLY":
                return totalAnnualAmmount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            case "QUARTERLY":
                return totalAnnualAmmount.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
            case "ANNUAL":
                return totalAnnualAmmount;
            default:
                return totalAnnualAmmount;
        }

    }
}
