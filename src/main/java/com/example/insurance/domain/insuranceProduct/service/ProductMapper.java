package com.example.insurance.domain.insuranceProduct.service;

import java.util.stream.Collectors;

import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.infrastructure.web.insuranceProduct.CategoryDto;
import com.example.insurance.infrastructure.web.insuranceProduct.InsuraceProductDto;

public class ProductMapper {

    public static InsuraceProductDto toDto(InsuranceProduct product) {
        InsuraceProductDto dto = new InsuraceProductDto();

        dto.setId(product.getId());
        dto.setProductCode(product.getProductCode());
        dto.setDisplayName(product.getDisplayName());
        dto.setDescription(product.getDescription());
        dto.setProductType(product.getProductType());
        dto.setBasePremium(product.getBasePremium());
        dto.setCoverageDetails(product.getCoverageDetails());
        dto.setEligibilityRules(product.getEligibilityRules());
        dto.setTargetAudience(product.getTargetAudience());
        dto.setRegions(product.getRegion());
        dto.setValidityPeriod(product.getValidityPeriod());
        dto.setTranslations(product.getTranslation());

        // Convert Enums to Strings
        dto.setAllowedClaimTypes(product.getAllowedClaimTypes().stream().map(Enum::name).collect(Collectors.toSet()));

        // Map Category to DTO
        if (product.getCategory() != null) {
            dto.setCategory(new CategoryDto(
                    product.getCategory().getId(),
                    product.getCategory().getName(),
                    product.getCategory().getDescription()));
        }

        return dto;

    }
}
