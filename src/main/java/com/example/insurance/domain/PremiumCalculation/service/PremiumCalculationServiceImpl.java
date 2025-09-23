package com.example.insurance.domain.PremiumCalculation.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.model.PremiumCalculationConfig;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;

@Service
public class PremiumCalculationServiceImpl implements PremiumCalculationService {

    public MonetaryAmount calculatePremium(InsuranceProduct product, Map<String, Object> riskFactors) {
        PremiumCalculationConfig config = product.getCalculationConfig();
        if (config == null) {
            throw new IllegalArgumentException("No calculation config found for product: " + product.getId());
        }

        MonetaryAmount basePremium = product.getBasePremium();
        BigDecimal calculatedAmount = basePremium.getAmount();

        // Apply formula-based calculation if specified
        if (config.getFormula() != null) {
            calculatedAmount = applyFormula(config.getFormula(), calculatedAmount, config.getFactors(), riskFactors);
        }

        // Apply age-based multiplier if applicable
        if (config.getAgeBrackets() != null && !config.getAgeBrackets().isEmpty() && riskFactors.containsKey("age")) {
            int age = (int) riskFactors.get("age");
            BigDecimal ageMultiplier = getAgeMultiplier(age, config.getAgeBrackets());
            calculatedAmount = calculatedAmount.multiply(ageMultiplier);
        }

        // Include tax if configured
        if (config.isIncludesTax() && config.getFactors().containsKey("taxRate")) {
            BigDecimal taxRate = config.getFactors().get("taxRate");
            calculatedAmount = calculatedAmount.multiply(BigDecimal.ONE.add(taxRate));
        }

        // Apply commission if configured
        if (config.getCommissionRate() != null) {
            calculatedAmount = calculatedAmount.multiply(BigDecimal.ONE.add(config.getCommissionRate()));
        }

        return new MonetaryAmount(
                calculatedAmount.setScale(2, RoundingMode.HALF_UP),
                basePremium.getCurrency());
    }

    private BigDecimal applyFormula(String formula, BigDecimal baseAmount,
            Map<String, BigDecimal> factors, Map<String, Object> riskFactors) {
        BigDecimal result = baseAmount;

        switch (formula) {
            case "AUTO_RISK_BASED":
                // Auto insurance formula: base * riskFactor * vehicleValueFactor
                BigDecimal riskFactor = (BigDecimal) riskFactors.getOrDefault("riskFactor", BigDecimal.ONE);
                BigDecimal vehicleValueFactor = factors.getOrDefault("vehicleValueFactor", BigDecimal.ONE);
                result = result.multiply(riskFactor).multiply(vehicleValueFactor);
                break;

            case "LIFE_AGE_BASED":
                // Life insurance formula: base * ageFactor * coverageAmountFactor
                BigDecimal ageFactor = factors.getOrDefault("ageFactor", BigDecimal.ONE);
                BigDecimal coverageFactor = factors.getOrDefault("coverageFactor", BigDecimal.ONE);
                result = result.multiply(ageFactor).multiply(coverageFactor);
                break;

            case "PROPERTY_VALUE_BASED":
                // Property insurance formula: base * propertyValueFactor * locationFactor
                BigDecimal propertyValueFactor = factors.getOrDefault("propertyValueFactor", BigDecimal.ONE);
                BigDecimal locationFactor = factors.getOrDefault("locationFactor", BigDecimal.ONE);
                result = result.multiply(propertyValueFactor).multiply(locationFactor);
                break;

            default:
                // Use base amount with no modifications
                break;
        }

        return result;
    }

    private BigDecimal getAgeMultiplier(int age, List<PremiumCalculationConfig.AgeBracket> ageBrackets) {
        for (PremiumCalculationConfig.AgeBracket bracket : ageBrackets) {
            if (age >= bracket.getMinAge() && age <= bracket.getMaxAge()) {
                return bracket.getMultiplier() != null ? bracket.getMultiplier() : bracket.getFactor();
            }
        }
        return BigDecimal.ONE;
    }

    public int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
