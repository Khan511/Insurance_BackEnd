package com.example.insurance.domain.insuranceProduct.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PremiumCalculationConfigDto {
    private String formula;
    private Map<String, BigDecimal> factors;
    private MonetaryAmount basePremium;
    private List<AgeBracketDto> ageBrackets;
    private Boolean includeTax;
    private BigDecimal commissionRate;

    @Data
    @Getter
    @Setter
    public static class AgeBracketDto {
        private Integer minAge;
        private Integer maxAge;
        private BigDecimal multiplier;
        private BigDecimal factor;
    }

}
