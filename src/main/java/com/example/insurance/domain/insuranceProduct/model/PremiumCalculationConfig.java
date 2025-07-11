package com.example.insurance.domain.insuranceProduct.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.util.StdConverter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PremiumCalculationConfig {
    private String formula;
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    @JsonDeserialize(contentConverter = StringToBigDecimalConverter.class)
    private Map<String, BigDecimal> factors = new HashMap<>();

    private List<AgeBracket> ageBrackets = new ArrayList<>();

    private boolean includesTax;

    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(converter = StringToBigDecimalConverter.class)
    private BigDecimal commissionRate = BigDecimal.ZERO;

    // Constructors
    public PremiumCalculationConfig() {
    }

    public PremiumCalculationConfig(String formula, Map<String, BigDecimal> factors,
            List<AgeBracket> ageBrackets, boolean includesTax,
            BigDecimal commissionRate) {
        this.formula = formula;
        this.factors = factors;
        this.ageBrackets = ageBrackets;
        this.includesTax = includesTax;
        this.commissionRate = commissionRate;
    }

    // Getters and Setters
    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Map<String, BigDecimal> getFactors() {
        return factors;
    }

    public void setFactors(Map<String, BigDecimal> factors) {
        this.factors = factors;
    }

    public List<AgeBracket> getAgeBrackets() {
        return ageBrackets;
    }

    public void setAgeBrackets(List<AgeBracket> ageBrackets) {
        this.ageBrackets = ageBrackets;
    }

    public boolean isIncludesTax() {
        return includesTax;
    }

    public void setIncludesTax(boolean includesTax) {
        this.includesTax = includesTax;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    // Helper method
    public void addFactor(String key, BigDecimal value) {
        this.factors.put(key, value);
    }

    // Static inner class
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AgeBracket {
        private int minAge;
        private int maxAge;
        @JsonSerialize(using = ToStringSerializer.class)
        @JsonDeserialize(converter = StringToBigDecimalConverter.class)
        private BigDecimal multiplier = BigDecimal.ONE;

        public AgeBracket() {
        }

        public AgeBracket(int minAge, int maxAge, BigDecimal multiplier) {
            this.minAge = minAge;
            this.maxAge = maxAge;
            this.multiplier = multiplier;
        }

        // Getters and Setters
        public int getMinAge() {
            return minAge;
        }

        public void setMinAge(int minAge) {
            this.minAge = minAge;
        }

        public int getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(int maxAge) {
            this.maxAge = maxAge;
        }

        public BigDecimal getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(BigDecimal multiplier) {
            this.multiplier = multiplier;
        }

    }

    public static class StringToBigDecimalConverter extends StdConverter<String, BigDecimal> {
        @Override
        public BigDecimal convert(String value) {
            return new BigDecimal(value);
        }
    }
}
// package com.example.insurance.domain.insuranceProduct.model;

// import java.math.BigDecimal;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
// import lombok.experimental.Accessors;

// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// public class PremiumCalculationConfig {
// private String formula;
// // private String formulaVersion;
// private Map<String, BigDecimal> factors = new HashMap<>();
// private List<AgeBracket> ageBrackets;
// private boolean includesTax;
// private BigDecimal commissionRate;

// // Add factor helper method
// public void addFactor(String key, BigDecimal value) {
// this.factors.put(key, value);
// }

// // Nested config objects
// @Getter
// @Setter
// @AllArgsConstructor
// @NoArgsConstructor
// public static class AgeBracket {
// private int minAge;
// private int maxAge;
// private BigDecimal multiplier;
// }
// }
