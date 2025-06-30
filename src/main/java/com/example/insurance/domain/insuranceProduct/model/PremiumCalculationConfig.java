package com.example.insurance.domain.insuranceProduct.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PremiumCalculationConfig {
    private String formulaVersion;
    private Map<String, BigDecimal> riskFactors;
    private List<AgeBracket> ageBrackets;
    private boolean includesTax;
    private BigDecimal commissionRate;

    // Nested config objects
    public static class AgeBracket {
        private int minAge;
        private int maxAge;
        private BigDecimal multiplier;
    }
}
