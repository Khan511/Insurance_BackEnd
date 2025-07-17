package com.example.insurance.infrastructure.web.insurancePolicy;

import lombok.Data;

@Data
public class BuyPolicyDto {

    private CustomerDto customer;
    private String product;
    private CoveragePeriodDto coveragePeriod;
    private PremiumDto premium;
    private BeneficiaryDto beneficiaries;
};
