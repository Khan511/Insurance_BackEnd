
// package com.example.insurance.domain.PremiumCalculation.service;

// import java.math.BigDecimal;
// import java.math.RoundingMode;
// import java.time.LocalDate;
// import java.time.Period;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.example.insurance.domain.customerPolicy.model.PaymentFrequency;
// import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
// import com.example.insurance.domain.insuranceProduct.model.PremiumCalculationConfig;
// import com.example.insurance.domain.insuranceProduct.repository.InsuranceProductRepository;
// import com.example.insurance.domain.user.model.User;
// import com.example.insurance.domain.user.repository.UserRepository;
// import com.example.insurance.infrastructure.web.premiumCalculation.PremiumCalculationRequest;
// import com.example.insurance.infrastructure.web.premiumCalculation.PremiumCalculationResponse;
// import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;

// import lombok.RequiredArgsConstructor;
// import net.objecthunter.exp4j.Expression;
// import net.objecthunter.exp4j.ExpressionBuilder;

// @Service
// @Transactional
// @RequiredArgsConstructor
// public class PremiumCalculationServiceImpl implements PremiumCalculationService {
//     private final InsuranceProductRepository insuranceProductRepository;
//     private final UserRepository userRepository;

//     @Override
//     public PremiumCalculationResponse calculatePremium(PremiumCalculationRequest request, String userEmail) {
//         // Get the current user
//         User user = userRepository.findByEmail(userEmail)
//                 .orElseThrow(() -> new RuntimeException("User not found"));

//         InsuranceProduct product = insuranceProductRepository.findById(request.getProductId())
//                 .orElseThrow(() -> new RuntimeException("Product not found"));

//         // Calculate age from user's date of birth
//         int userAge = calculateAge(user.getDateOfBirth());

//         // Prepare enhanced risk factors with age
//         Map<String, Object> enhancedRiskFactors = new HashMap<>(request.getRiskFactors());
//         enhancedRiskFactors.put("age", userAge);

//         // For Auto insurance, also add driverAge
//         if (product.getProductType().name().equals("AUTO")) {
//             enhancedRiskFactors.put("driverAge", userAge);
//         }
//         // For Life insurance, also add insuredAge
//         else if (product.getProductType().name().equals("LIFE")) {
//             enhancedRiskFactors.put("insuredAge", userAge);
//         }

//         // Calculate the premium using the core calculation logic
//         MonetaryAmount calculatedPremium = calculatePremiumInternal(product, enhancedRiskFactors);

//         // Calculate installment amount based on payment frequency
//         BigDecimal installmentAmount = calculateInstallmentAmount(calculatedPremium.getAmount(),
//                 request.getPaymentFrequency());

//         return new PremiumCalculationResponse(
//                 calculatedPremium.getAmount(),
//                 calculatedPremium.getCurrency(),
//                 installmentAmount,
//                 request.getPaymentFrequency(),
//                 product.getCalculationConfig().getFormula());
//     }

//     // Rename your existing calculatePremium method to avoid conflict
//     private MonetaryAmount calculatePremiumInternal(InsuranceProduct product, Map<String, Object> riskFactors) {
//         PremiumCalculationConfig config = product.getCalculationConfig();
//         if (config == null) {
//             throw new IllegalArgumentException("No calculation config found for product: " + product.getId());
//         }

//         MonetaryAmount basePremium = product.getBasePremium();
//         BigDecimal calculatedAmount = basePremium.getAmount();

//         // Apply formula-based calculation if specified
//         if (config.getFormula() != null) {
//             calculatedAmount = applyFormula(config.getFormula(), calculatedAmount, config.getFactors(), riskFactors);
//         }

//         // Apply age-based multiplier if applicable
//         if (config.getAgeBrackets() != null && !config.getAgeBrackets().isEmpty() &&
//                 riskFactors.containsKey("age")) {
//             int age = (int) riskFactors.get("age");
//             BigDecimal ageMultiplier = getAgeMultiplier(age, config.getAgeBrackets());
//             calculatedAmount = calculatedAmount.multiply(ageMultiplier);
//         }

//         // Include tax if configured
//         if (config.isIncludesTax() && config.getFactors().containsKey("taxRate")) {
//             BigDecimal taxRate = config.getFactors().get("taxRate");
//             calculatedAmount = calculatedAmount.multiply(BigDecimal.ONE.add(taxRate));
//         }

//         // Apply commission if configured
//         if (config.getCommissionRate() != null) {
//             calculatedAmount = calculatedAmount.multiply(BigDecimal.ONE.add(config.getCommissionRate()));
//         }

//         return new MonetaryAmount(
//                 calculatedAmount.setScale(2, RoundingMode.HALF_UP),
//                 basePremium.getCurrency());
//     }

//     private BigDecimal calculateInstallmentAmount(BigDecimal totalAnnualAmount, PaymentFrequency paymentFrequency) {
//         if (paymentFrequency == null) {
//             return totalAnnualAmount;
//         }

//         switch (paymentFrequency) {
//             case PaymentFrequency.MONTHLY:
//                 return totalAnnualAmount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
//             case PaymentFrequency.QUARTERLY:
//                 return totalAnnualAmount.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
//             case PaymentFrequency.ANNUAL:
//                 return totalAnnualAmount;
//             default:
//                 return totalAnnualAmount;
//         }
//     }

//     // Keep all your existing helper methods unchanged
//     private BigDecimal applyFormula(String formula, BigDecimal baseAmount,
//             Map<String, BigDecimal> factors, Map<String, Object> riskFactors) {

//         if (formula == null || formula.trim().isEmpty()) {
//             return baseAmount;
//         }

//         Map<String, BigDecimal> variables = new HashMap<>();
//         variables.put("base", baseAmount);

//         // Add factor values
//         for (Map.Entry<String, BigDecimal> entry : factors.entrySet()) {
//             variables.put(entry.getKey(), entry.getValue());
//         }

//         // Add ALL risk factors with proper type conversion
//         for (Map.Entry<String, Object> entry : riskFactors.entrySet()) {
//             String key = entry.getKey();
//             Object value = entry.getValue();

//             try {
//                 if (value instanceof Integer) {
//                     variables.put(key, BigDecimal.valueOf((Integer) value));
//                 } else if (value instanceof BigDecimal) {
//                     variables.put(key, (BigDecimal) value);
//                 } else if (value instanceof Double) {
//                     variables.put(key, BigDecimal.valueOf((Double) value));
//                 } else if (value instanceof String) {
//                     // Handle string values that should be numbers
//                     if (key.equals("propertyLocation")) {
//                         // Convert location string to risk zone number
//                         String location = (String) value;
//                         BigDecimal riskZone = switch (location.toUpperCase()) {
//                             case "HIGH_RISK" -> new BigDecimal("3.0");
//                             case "MEDIUM_RISK" -> new BigDecimal("2.0");
//                             case "LOW_RISK" -> new BigDecimal("1.0");
//                             default -> new BigDecimal("2.0");
//                         };
//                         variables.put("riskZone", riskZone);
//                     } else if (key.equals("healthCondition")) {
//                         // Convert health condition to risk level
//                         String health = (String) value;
//                         int riskLevel = switch (health.toUpperCase()) {
//                             case "POOR" -> 3;
//                             case "FAIR" -> 2;
//                             case "GOOD" -> 1;
//                             case "EXCELLENT" -> 0;
//                             default -> 0;
//                         };
//                         variables.put("riskLevel", BigDecimal.valueOf(riskLevel));
//                     }
//                 }
//             } catch (Exception e) {
//                 System.err.println("Error processing risk factor " + key + ": " + e.getMessage());
//             }
//         }

//         // Ensure age-related variables are properly mapped
//         if (variables.containsKey("age")) {
//             BigDecimal age = variables.get("age");
//             if (formula.contains("driverAge") && !variables.containsKey("driverAge")) {
//                 variables.put("driverAge", age);
//             }
//             if (formula.contains("insuredAge") && !variables.containsKey("insuredAge")) {
//                 variables.put("insuredAge", age);
//             }
//         }

//         try {
//             BigDecimal result = evaluateExpression(formula, variables);
//             System.out.println("Formula evaluation successful. Result: " + result);
//             return result;
//         } catch (Exception e) {
//             System.err.println("Error evaluating formula: " + formula + ", error: " + e.getMessage());
//             System.err.println("Falling back to base amount: " + baseAmount);
//             return baseAmount;
//         }
//     }

//     private BigDecimal evaluateExpression(String expression, Map<String, BigDecimal> variables) {
//         try {
//             // Build expression
//             ExpressionBuilder builder = new ExpressionBuilder(expression);

//             // Set variables
//             for (Map.Entry<String, BigDecimal> entry : variables.entrySet()) {
//                 builder.variable(entry.getKey());
//             }

//             Expression exp = builder.build();

//             // Set variable values
//             for (Map.Entry<String, BigDecimal> entry : variables.entrySet()) {
//                 exp.setVariable(entry.getKey(), entry.getValue().doubleValue());
//             }

//             // Evaluate
//             double result = exp.evaluate();

//             return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);

//         } catch (Exception e) {
//             throw new IllegalArgumentException("Invalid formula: " + expression, e);
//         }
//     }

//     private BigDecimal getAgeMultiplier(int age, List<PremiumCalculationConfig.AgeBracket> ageBrackets) {
//         if (ageBrackets == null || ageBrackets.isEmpty()) {
//             return BigDecimal.ONE;
//         }
//         for (PremiumCalculationConfig.AgeBracket bracket : ageBrackets) {
//             if (age >= bracket.getMinAge() && age <= bracket.getMaxAge()) {
//                 return bracket.getMultiplier() != null ? bracket.getMultiplier() : bracket.getFactor();
//             }
//         }
//         return BigDecimal.ONE;
//     }

//     @Override
//     public int calculateAge(LocalDate birthDate) {
//         if (birthDate == null) {
//             return 0;
//         }
//         return Period.between(birthDate, LocalDate.now()).getYears();
//     }
// }

package com.example.insurance.domain.PremiumCalculation.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.insurance.domain.customerPolicy.model.PaymentFrequency;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.model.PremiumCalculationConfig;
import com.example.insurance.domain.insuranceProduct.repository.InsuranceProductRepository;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.repository.UserRepository;
import com.example.insurance.infrastructure.web.premiumCalculation.PremiumCalculationRequest;
import com.example.insurance.infrastructure.web.premiumCalculation.PremiumCalculationResponse;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PremiumCalculationServiceImpl implements PremiumCalculationService {
    private final InsuranceProductRepository insuranceProductRepository;
    private final UserRepository userRepository;

    @Override
    public PremiumCalculationResponse calculatePremium(PremiumCalculationRequest request, String userEmail) {
        // Get the product
        InsuranceProduct product = insuranceProductRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int userAge = 0;
        boolean isAuthenticated = false;

        // Case 1: User is authenticated - get age from database
        if (userEmail != null && !userEmail.isEmpty()) {
            try {
                User user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                userAge = calculateAge(user.getDateOfBirth());
                isAuthenticated = true;
            } catch (Exception e) {
                // If user not found, treat as unauthenticated
                System.err.println("User not found: " + userEmail + ", treating as unauthenticated");
            }
        }

        // // Case 2: User is not authenticated - get age from risk factors
        // if (!isAuthenticated) {
        // Map<String, Object> riskFactors = request.getRiskFactors();

        // // Check if age is provided in risk factors
        // if (riskFactors.containsKey("age")) {
        // try {
        // Object ageObj = riskFactors.get("age");
        // if (ageObj instanceof Integer) {
        // userAge = (Integer) ageObj;
        // } else if (ageObj instanceof String) {
        // userAge = Integer.parseInt((String) ageObj);
        // } else if (ageObj instanceof Number) {
        // userAge = ((Number) ageObj).intValue();
        // }

        // // Validate age for insurance type
        // validateAgeForInsurance(userAge, product.getProductType().name());

        // } catch (NumberFormatException e) {
        // throw new IllegalArgumentException("Invalid age provided in risk factors");
        // }
        // } else {
        // // For AUTO and LIFE insurance, age must be provided if not authenticated
        // if (product.getProductType().name().equals("AUTO") ||
        // product.getProductType().name().equals("LIFE")) {
        // throw new IllegalArgumentException(
        // "Age is required for " + product.getProductType() + " insurance premium
        // calculation. " +
        // "Please provide your date of birth or age.");
        // }
        // }
        // }

        // Prepare enhanced risk factors with age
        Map<String, Object> enhancedRiskFactors = new HashMap<>(request.getRiskFactors());

        // Only add age if not already present (frontend might have sent it)
        if (!enhancedRiskFactors.containsKey("age")) {
            enhancedRiskFactors.put("age", userAge);
        }

        // For Auto insurance, also add driverAge
        if (product.getProductType().name().equals("AUTO")) {
            enhancedRiskFactors.put("driverAge", userAge);
        }
        // For Life insurance, also add insuredAge
        else if (product.getProductType().name().equals("LIFE")) {
            enhancedRiskFactors.put("insuredAge", userAge);
        }

        // Calculate the premium using the core calculation logic
        MonetaryAmount calculatedPremium = calculatePremiumInternal(product, enhancedRiskFactors);

        // Calculate installment amount based on payment frequency
        BigDecimal installmentAmount = calculateInstallmentAmount(
                calculatedPremium.getAmount(),
                request.getPaymentFrequency());

        return new PremiumCalculationResponse(
                calculatedPremium.getAmount(),
                calculatedPremium.getCurrency(),
                installmentAmount,
                request.getPaymentFrequency(),
                product.getCalculationConfig().getFormula());
    }

    // Validate age for specific insurance types
    private void validateAgeForInsurance(int age, String insuranceType) {
        switch (insuranceType) {
            case "AUTO":
                if (age < 18 || age > 85) {
                    throw new IllegalArgumentException("For auto insurance, age must be between 18-85 years");
                }
                break;
            case "LIFE":
                if (age < 18 || age > 70) {
                    throw new IllegalArgumentException("For life insurance, age must be between 18-70 years");
                }
                break;
            case "PROPERTY":
                if (age != 0 && (age < 18 || age > 100)) {
                    throw new IllegalArgumentException("Age must be between 18-100 years for property insurance");
                }
                break;
            default:
                if (age < 18 || age > 100) {
                    throw new IllegalArgumentException("Age must be between 18-100 years");
                }
        }
    }

    // Rename your existing calculatePremium method to avoid conflict
    private MonetaryAmount calculatePremiumInternal(InsuranceProduct product, Map<String, Object> riskFactors) {
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
            int age;
            try {
                Object ageObj = riskFactors.get("age");
                if (ageObj instanceof Integer) {
                    age = (Integer) ageObj;
                } else if (ageObj instanceof String) {
                    age = Integer.parseInt((String) ageObj);
                } else if (ageObj instanceof Number) {
                    age = ((Number) ageObj).intValue();
                } else {
                    age = 0;
                }
            } catch (Exception e) {
                age = 0;
            }

            if (age > 0) {
                BigDecimal ageMultiplier = getAgeMultiplier(age, config.getAgeBrackets());
                calculatedAmount = calculatedAmount.multiply(ageMultiplier);
            }
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

    private BigDecimal calculateInstallmentAmount(BigDecimal totalAnnualAmount, PaymentFrequency paymentFrequency) {
        if (paymentFrequency == null) {
            return totalAnnualAmount;
        }

        switch (paymentFrequency) {
            case MONTHLY:
                return totalAnnualAmount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            case QUARTERLY:
                return totalAnnualAmount.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
            case ANNUAL:
                return totalAnnualAmount;
            default:
                return totalAnnualAmount;
        }
    }

    // Keep all your existing helper methods unchanged (applyFormula,
    // evaluateExpression, getAgeMultiplier)
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
            net.objecthunter.exp4j.ExpressionBuilder builder = new net.objecthunter.exp4j.ExpressionBuilder(expression);

            // Set variables
            for (Map.Entry<String, BigDecimal> entry : variables.entrySet()) {
                builder.variable(entry.getKey());
            }

            net.objecthunter.exp4j.Expression exp = builder.build();

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

    private BigDecimal getAgeMultiplier(int age, java.util.List<PremiumCalculationConfig.AgeBracket> ageBrackets) {
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

    @Override
    public int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}