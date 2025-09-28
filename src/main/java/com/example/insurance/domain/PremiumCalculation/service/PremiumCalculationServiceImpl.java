package com.example.insurance.domain.PremiumCalculation.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.model.PremiumCalculationConfig;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

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
        if (config.getAgeBrackets() != null && !config.getAgeBrackets().isEmpty() &&
                riskFactors.containsKey("age")) {
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
        System.out.println("=== PREMIUM CALCULATION ===");
        System.out.println("Base: " + basePremium.getAmount());
        System.out.println("Factors: " + config.getFactors());
        System.out.println("Risk Factors: " + riskFactors);
        System.out.println("Formula: " + config.getFormula());
        System.out.println("Calculated Premium: " + calculatedAmount);
        System.out.println("===========================");

        return new MonetaryAmount(
                calculatedAmount.setScale(2, RoundingMode.HALF_UP),
                basePremium.getCurrency());
    }

    private BigDecimal applyFormula(String formula, BigDecimal baseAmount,
            Map<String, BigDecimal> factors, Map<String, Object> riskFactors) {

        if (formula == null || formula.trim().isEmpty()) {
            return baseAmount;
        }

        Map<String, BigDecimal> variables = new HashMap<>();
        variables.put("base", baseAmount);

        // Add factor values
        for (Map.Entry<String, BigDecimal> entry : factors.entrySet()) {
            variables.put(entry.getKey(), entry.getValue());
        }

        System.out.println("=== FORMULA DEBUG ===");
        System.out.println("Formula: " + formula);
        System.out.println("Base amount: " + baseAmount);
        System.out.println("Factors: " + factors);
        System.out.println("Risk factors: " + riskFactors);

        // Add ALL risk factors with proper type conversion
        for (Map.Entry<String, Object> entry : riskFactors.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            try {
                if (value instanceof Integer) {
                    variables.put(key, BigDecimal.valueOf((Integer) value));
                } else if (value instanceof BigDecimal) {
                    variables.put(key, (BigDecimal) value);
                } else if (value instanceof Double) {
                    variables.put(key, BigDecimal.valueOf((Double) value));
                } else if (value instanceof String) {
                    // Handle string values that should be numbers
                    if (key.equals("propertyLocation")) {
                        // Convert location string to risk zone number
                        String location = (String) value;
                        BigDecimal riskZone = switch (location.toUpperCase()) {
                            case "HIGH_RISK" -> new BigDecimal("3.0");
                            case "MEDIUM_RISK" -> new BigDecimal("2.0");
                            case "LOW_RISK" -> new BigDecimal("1.0");
                            default -> new BigDecimal("2.0");
                        };
                        variables.put("riskZone", riskZone);
                    } else if (key.equals("healthCondition")) {
                        // Convert health condition to risk level
                        String health = (String) value;
                        int riskLevel = switch (health.toUpperCase()) {
                            case "POOR" -> 3;
                            case "FAIR" -> 2;
                            case "GOOD" -> 1;
                            case "EXCELLENT" -> 0;
                            default -> 0;
                        };
                        variables.put("riskLevel", BigDecimal.valueOf(riskLevel));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing risk factor " + key + ": " + e.getMessage());
            }
        }

        // Ensure age-related variables are properly mapped
        if (variables.containsKey("age")) {
            BigDecimal age = variables.get("age");
            if (formula.contains("driverAge") && !variables.containsKey("driverAge")) {
                variables.put("driverAge", age);
            }
            if (formula.contains("insuredAge") && !variables.containsKey("insuredAge")) {
                variables.put("insuredAge", age);
            }
        }

        System.out.println("All variables for evaluation: " + variables);

        try {
            BigDecimal result = evaluateExpression(formula, variables);
            System.out.println("Formula evaluation successful. Result: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Error evaluating formula: " + formula + ", error: " + e.getMessage());
            System.err.println("Falling back to base amount: " + baseAmount);
            return baseAmount;
        }
    }

    private BigDecimal evaluateExpression(String expression, Map<String, BigDecimal> variables) {
        try {
            // Build expression
            ExpressionBuilder builder = new ExpressionBuilder(expression);

            // Set variables
            for (Map.Entry<String, BigDecimal> entry : variables.entrySet()) {
                builder.variable(entry.getKey());
            }

            Expression exp = builder.build();

            // Set variable values
            for (Map.Entry<String, BigDecimal> entry : variables.entrySet()) {
                exp.setVariable(entry.getKey(), entry.getValue().doubleValue());
            }

            // Evaluate
            double result = exp.evaluate();

            return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid formula: " + expression, e);
        }
    }

    private BigDecimal getAgeMultiplier(int age, List<PremiumCalculationConfig.AgeBracket> ageBrackets) {
        if (ageBrackets == null || ageBrackets.isEmpty()) {
            return BigDecimal.ONE;
        }
        for (PremiumCalculationConfig.AgeBracket bracket : ageBrackets) {
            if (age >= bracket.getMinAge() && age <= bracket.getMaxAge()) {
                return bracket.getMultiplier() != null ? bracket.getMultiplier() : bracket.getFactor();
            }
        }
        return BigDecimal.ONE;
    }

    public int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
