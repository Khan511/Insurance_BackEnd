package com.example.insurance.domain.insuranceProduct.service;

import java.util.List;
import java.util.stream.Collectors;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.model.PremiumCalculationConfig;
import com.example.insurance.shared.kernel.dtos.CategoryDto;
import com.example.insurance.shared.kernel.dtos.InsuraceProductDto;

public class ProductMapper {

    public static InsuraceProductDto toDto(InsuranceProduct product) {
        InsuraceProductDto dto = new InsuraceProductDto();

        dto.setId(product.getId());
        dto.setPolicyNumber(product.getPolicyNumber());
        dto.setProductCode(product.getProductCode());
        dto.setDisplayName(product.getDisplayName());
        dto.setDescription(product.getDescription());
        dto.setProductType(product.getProductType());
        // dto.setBasePremium(product.getBasePremium());
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

        if (product.getCalculationConfig() != null) {
            dto.setCalculationConfig(mapCalculationConfig(product.getCalculationConfig(), product));
        }
        return dto;
    }

    private static PremiumCalculationConfigDto mapCalculationConfig(PremiumCalculationConfig config,
            InsuranceProduct product) {
        PremiumCalculationConfigDto dto = new PremiumCalculationConfigDto();

        dto.setFormula(config.getFormula());
        dto.setFactors(config.getFactors());
        dto.setIncludeTax(config.isIncludesTax());
        dto.setBasePremium(product.getBasePremium());
        dto.setCommissionRate(config.getCommissionRate());

        // Map age brackets
        if (config.getAgeBrackets() != null) {
            List<PremiumCalculationConfigDto.AgeBracketDto> ageBracketDtos = config.getAgeBrackets().stream()
                    .map(bracket -> {
                        PremiumCalculationConfigDto.AgeBracketDto bracketDto = new PremiumCalculationConfigDto.AgeBracketDto();

                        bracketDto.setMinAge(bracket.getMinAge());
                        bracketDto.setMaxAge(bracket.getMaxAge());
                        bracketDto.setFactor(bracket.getFactor());

                        return bracketDto;
                    })
                    .collect(Collectors.toList());

            dto.setAgeBrackets(ageBracketDtos);
        }
        return dto;
    }
}
