package com.example.insurance.infrastructure.web.custommerPolicy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.insurance.common.enummuration.PolicyStatus;
import com.example.insurance.common.enummuration.ProductType;
import com.example.insurance.domain.customerPolicy.model.PaymentFrequency;
import com.example.insurance.domain.insuranceProduct.service.PremiumCalculationConfigDto;
import com.example.insurance.embeddable.CoverageDetail;
import com.example.insurance.shared.kernel.dtos.CategoryDto;
import com.example.insurance.embeddable.ProductTranslation;
import com.example.insurance.shared.kernel.embeddables.PolicyPeriod;
import lombok.Data;

@Data
public class InsurancePolicyDto {

    private Long id;
    private String PolicyNumber;
    private String productCode;
    private String displayName;
    private String description;
    private ProductType productType;
    private PremiumCalculationConfigDto calculationConfig;
    private Set<CoverageDetail> coverageDetails;
    private Map<String, String> eligibilityRules;
    private PolicyStatus status;

    private List<String> targetAudience;
    private List<String> regions;
    private CategoryDto category;
    private PolicyPeriod validityPeriod;
    private Set<String> allowedClaimTypes; // Using String instead of enum
    private Map<String, ProductTranslation> translations;

    // Premium information
    private BigDecimal premium;
    private String currency;

    // Payment Information
    // MONTHLY, QUARTERLY, ANNUAL
    private PaymentFrequency paymentFrequency;
    // Payment schedules
    private List<PaymentScheduleDto> paymentSchedules;

    // Beneficiaries
    private List<BeneficiaryDto> beneficiaries;

}
