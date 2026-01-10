package com.example.insurance.infrastructure.web.premiumCalculation;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.insurance.domain.PremiumCalculation.service.PremiumCalculationService;
import com.example.insurance.global.config.CustomUserDetails;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/premium")
public class PremiumCalculationController {
    private final PremiumCalculationService premiumCalculationService;

    @PostMapping("/calculate")
    public PremiumCalculationResponse calculatePremium(
            @RequestBody PremiumCalculationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return premiumCalculationService.calculatePremium(request, userDetails.getUsername());
    }
}
