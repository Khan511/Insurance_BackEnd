package com.example.insurance.domain.customerPolicy.service;

import java.util.stream.Collectors;
import com.example.insurance.common.enummuration.Relationship;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;
import com.example.insurance.domain.policyBeneficiary.model.PolicyBeneficiary;
import com.example.insurance.embeddable.BeneficiaryDetails;
import com.example.insurance.infrastructure.web.custommerPolicy.BeneficiaryDto;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.infrastructure.web.custommerPolicy.PaymentScheduleDto;
import com.example.insurance.shared.kernel.dtos.CategoryDto;

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

    // Map PolicyBeneficiary entity -> BeneficiaryDto
    private static BeneficiaryDto mapPolicyBeneficiaryToDto(PolicyBeneficiary entity) {
        if (entity == null || entity.getDetails() == null)
            return null;

        BeneficiaryDto dto = new BeneficiaryDto();

        dto.setName(entity.getDetails().getFullLegalname());
        dto.setRelationship(entity.getDetails().getRelationship().name());
        dto.setDateOfBirth(entity.getDetails().getDateOfBirth());

        return dto;
    }

    public static InsurancePolicyDto toDto(CustomerPolicy product) {
        InsurancePolicyDto dto = new InsurancePolicyDto();

        dto.setId(product.getId());
        dto.setPolicyNumber(product.getProduct().getPolicyNumber());
        dto.setProductCode(product.getProduct().getProductCode());
        dto.setDisplayName(product.getProduct().getDisplayName());
        dto.setDescription(product.getProduct().getDescription());
        dto.setProductType(product.getProduct().getProductType());
        dto.setPremium(product.getPremium().getAmount());
        dto.setCurrency(product.getPremium().getCurrency());
        dto.setStatus(product.getStatus());
        dto.setCoverageDetails(product.getProduct().getCoverageDetails());
        dto.setEligibilityRules(product.getProduct().getEligibilityRules());
        dto.setTargetAudience(product.getProduct().getTargetAudience());
        dto.setRegions(product.getProduct().getRegion());
        dto.setValidityPeriod(product.getProduct().getValidityPeriod());
        dto.setTranslations(product.getProduct().getTranslation());
        dto.setPaymentFrequency(product.getPaymentFrequency());
        dto.setBeneficiaries(
                product.getBeneficiaries().stream().map((bene) -> mapPolicyBeneficiaryToDto(bene))
                        .collect(Collectors.toList()));

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
        // Map payment schedules
        if (product.getPaymentSchedules() != null) {
            dto.setPaymentSchedules(
                    product.getPaymentSchedules().stream()
                            .map(PolicyMapper::mapToPaymentScheduleDto)
                            .collect(Collectors.toList()));
        }
        return dto;
    }

    // New method to map PaymentSchedule to PaymentScheduleDto
    public static PaymentScheduleDto mapToPaymentScheduleDto(PaymentSchedule paymentSchedule) {
        PaymentScheduleDto dto = new PaymentScheduleDto();
        dto.setId(paymentSchedule.getId());

        if (paymentSchedule.getDueAmount() != null) {
            dto.setDueAmount(paymentSchedule.getDueAmount().getAmount());
            dto.setCurrency(paymentSchedule.getDueAmount().getCurrency());
        }

        dto.setDueDate(paymentSchedule.getDueDate());
        dto.setPaidDate(paymentSchedule.getPaidDate());

        // You can add status calculation if needed
        if (paymentSchedule.getPaidDate() != null) {
            dto.setStatus("PAID");
        } else if (paymentSchedule.getDueDate().isBefore(java.time.LocalDate.now())) {
            dto.setStatus("OVERDUE");
        } else {
            dto.setStatus("PENDING");
        }

        return dto;
    }
}
