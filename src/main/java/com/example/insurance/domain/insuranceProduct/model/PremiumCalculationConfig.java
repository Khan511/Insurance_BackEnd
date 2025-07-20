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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    // Helper method
    public void addFactor(String key, BigDecimal value) {
        this.factors.put(key, value);
    }

    // Static inner class
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AgeBracket {
        private int minAge;
        private int maxAge;
        @JsonSerialize(using = ToStringSerializer.class)
        @JsonDeserialize(converter = StringToBigDecimalConverter.class)
        private BigDecimal multiplier = BigDecimal.ONE;

        private BigDecimal factor;
    }

    public static class StringToBigDecimalConverter extends StdConverter<String, BigDecimal> {
        @Override
        public BigDecimal convert(String value) {
            return new BigDecimal(value);
        }
    }
}
