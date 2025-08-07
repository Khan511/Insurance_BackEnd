package com.example.insurance.infrastructure.web.custommerPolicy;

import java.util.List;

import lombok.Data;

@Data
public class BuyPolicyDto {
    private Long policyId;
    private String policyNumber;
    private String status;
    private CustomerDto customer;
    private String product;
    private CoveragePeriodDto coveragePeriod;
    private PremiumDto premium;
    private List<BeneficiaryDto> beneficiaries;
};
