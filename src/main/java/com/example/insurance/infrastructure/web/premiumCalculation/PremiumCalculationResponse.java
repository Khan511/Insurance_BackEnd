package com.example.insurance.infrastructure.web.premiumCalculation;

import java.math.BigDecimal;

import com.example.insurance.domain.customerPolicy.model.PaymentFrequency;

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
    private PaymentFrequency paymentFrequency;
    private String formulaUsed;

}
