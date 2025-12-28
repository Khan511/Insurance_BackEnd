package com.example.insurance.infrastructure.web.custommerPolicy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private String statusChangeNotes;

    private List<String> targetAudience;
    private List<String> regions;
    private CategoryDto category;
    private PolicyPeriod validityPeriod;
    private Set<String> allowedClaimTypes;
    private Map<String, ProductTranslation> translations;

    private String policyHolderName;
    private String policyHolderEmail;

    // Premium information
    private BigDecimal premium;
    private String currency;

    // Cancellation Fields
    private String cancellationReason;
    private LocalDate cancellationDate;
    private String cancelledBy;

    // AUDIT FIELDS
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // RELATIONSHIP FIELDS:
    private String userId;
    private Long policyHolderId;
    private Long productId;

    // Payment Information
    // MONTHLY, QUARTERLY, ANNUAL
    private PaymentFrequency paymentFrequency;

    // Payment schedules
    private List<PaymentScheduleDto> paymentSchedules;

    // Beneficiaries
    private List<BeneficiaryDto> beneficiaries;

}
