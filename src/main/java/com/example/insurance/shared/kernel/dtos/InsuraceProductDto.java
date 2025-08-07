package com.example.insurance.shared.kernel.dtos;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.insurance.common.enummuration.ProductType;
import com.example.insurance.domain.insuranceProduct.service.PremiumCalculationConfigDto;
import com.example.insurance.embeddable.CoverageDetail;
import com.example.insurance.embeddable.ProductTranslation;
import com.example.insurance.shared.kernel.embeddables.PolicyPeriod;
import lombok.Data;

@Data
public class InsuraceProductDto {
    private Long id;
    private String PolicyNumber;
    private String productCode;
    private String displayName;
    private String description;
    private ProductType productType;
    private PremiumCalculationConfigDto calculationConfig;
    private Set<CoverageDetail> coverageDetails;
    private Map<String, String> eligibilityRules;

    private List<String> targetAudience;
    private List<String> regions;
    private CategoryDto category;
    private PolicyPeriod validityPeriod;
    private Set<String> allowedClaimTypes; // Using String instead of enum
    private Map<String, ProductTranslation> translations;
}
