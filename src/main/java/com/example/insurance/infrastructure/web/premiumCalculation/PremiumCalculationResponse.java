package com.example.insurance.infrastructure.web.premiumCalculation;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PremiumCalculationResponse {

    private BigDecimal amount;
    private String currency;
    private BigDecimal installmentAmount;
    private String paymentFrequency;
    private String formulaUsed;

}
