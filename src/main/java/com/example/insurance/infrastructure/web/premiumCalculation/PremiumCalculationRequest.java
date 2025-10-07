package com.example.insurance.infrastructure.web.premiumCalculation;

import java.util.Map;

import com.example.insurance.domain.customerPolicy.model.PaymentFrequency;

import lombok.Data;

@Data
public class PremiumCalculationRequest {
    private Long productId;
    private Map<String, Object> riskFactors;
    // private String paymentFrequency;
    private PaymentFrequency paymentFrequency;

}
