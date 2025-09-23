package com.example.insurance.infrastructure.web.custommerPolicy;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class BuyPolicyDto {
    private Long productId;
    private String policyNumber;
    private String status;
    private CustomerDto customer;
    private String product;
    private CoveragePeriodDto coveragePeriod;
    private PremiumDto premium;
    private List<BeneficiaryDto> beneficiaries;

    private BigDecimal vehicleValue; // For auto insurance
    private Integer drivingExperience; // For auto insurance
    private String healthCondition; // For life insurance
    private BigDecimal propertyValue; // For home insurance
    private String propertyLocation; // For home insurance
};
