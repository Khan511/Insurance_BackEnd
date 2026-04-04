
package com.example.insurance.infrastructure.web.premiumCalculation;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            @RequestBody PremiumCalculationRequest request) {
 
        // Check if user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)
                && authentication.getPrincipal() instanceof CustomUserDetails) {
            // User is authenticated
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            userEmail = userDetails.getUsername();
        }

        return premiumCalculationService.calculatePremium(request, userEmail);
    }
}