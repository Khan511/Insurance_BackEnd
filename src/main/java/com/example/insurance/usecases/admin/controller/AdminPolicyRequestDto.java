package com.example.insurance.usecases.admin.controller;

import java.math.BigDecimal;

// import java.math.BigDecimal;
// import java.util.List;

import com.example.insurance.domain.customerPolicy.model.PaymentFrequency;
import com.example.insurance.infrastructure.web.custommerPolicy.CoveragePeriodDto;
import com.example.insurance.infrastructure.web.custommerPolicy.PremiumDto;

import lombok.Data;

@Data
public class AdminPolicyRequestDto {
    private Long id;
    // private String policyNumber;
    private String status;
    // private CustomerDto customer;
    // private String product;
    private CoveragePeriodDto validityPeriod;
    private BigDecimal premium;
    // private List<BeneficiaryDto> beneficiaries;

    // private BigDecimal vehicleValue; // For auto insurance
    // private Integer drivingExperience; // For auto insurance
    // private String healthCondition; // For life insurance
    // private BigDecimal propertyValue; // For home insurance
    // private String propertyLocation; // For home insurance

    // private PaymentFrequency paymentFrequency;
    private String paymentFrequency;
};
