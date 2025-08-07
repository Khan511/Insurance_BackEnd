package com.example.insurance.domain.customerPolicy.service;

import java.util.stream.Collectors;
import com.example.insurance.common.enummuration.Relationship;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.policyBeneficiary.model.PolicyBeneficiary;
import com.example.insurance.embeddable.BeneficiaryDetails;
import com.example.insurance.infrastructure.web.custommerPolicy.BeneficiaryDto;
import com.example.insurance.shared.kernel.dtos.CategoryDto;
import com.example.insurance.shared.kernel.dtos.InsuraceProductDto;

public class PolicyMapper {
    public static PolicyBeneficiary mapToBeneficiaryEntity(BeneficiaryDto dto) {

        PolicyBeneficiary beneficiary = new PolicyBeneficiary();
        beneficiary.setDetails(mapBeneficiaryDetails(dto));
        return beneficiary;
    }

    private static BeneficiaryDetails mapBeneficiaryDetails(BeneficiaryDto dto) {
        BeneficiaryDetails details = new BeneficiaryDetails();

        details.setFullLegalname(dto.getName());
        details.setRelationship(Relationship.valueOf(dto.getRelationship()));
        details.setDateOfBirth(dto.getDateOfBirth());
        return details;
    };

    public static InsuraceProductDto toDto(CustomerPolicy product) {
        InsuraceProductDto dto = new InsuraceProductDto();

        dto.setId(product.getId());
        dto.setPolicyNumber(product.getProduct().getPolicyNumber());
        dto.setProductCode(product.getProduct().getProductCode());
        dto.setDisplayName(product.getProduct().getDisplayName());
        dto.setDescription(product.getProduct().getDescription());
        dto.setProductType(product.getProduct().getProductType());
        // dto.setBasePremium(product.getBasePremium());
        dto.setCoverageDetails(product.getProduct().getCoverageDetails());
        dto.setEligibilityRules(product.getProduct().getEligibilityRules());
        dto.setTargetAudience(product.getProduct().getTargetAudience());
        dto.setRegions(product.getProduct().getRegion());
        dto.setValidityPeriod(product.getProduct().getValidityPeriod());
        dto.setTranslations(product.getProduct().getTranslation());

        // Convert Enums to Strings
        dto.setAllowedClaimTypes(
                product.getProduct().getAllowedClaimTypes().stream().map(Enum::name).collect(Collectors.toSet()));

        // Map Category to DTO
        if (product.getProduct().getCategory() != null) {
            dto.setCategory(new CategoryDto(
                    product.getProduct().getCategory().getId(),
                    product.getProduct().getCategory().getName(),
                    product.getProduct().getCategory().getDescription()));
        }
        return dto;
    }
}
