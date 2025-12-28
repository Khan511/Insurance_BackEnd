package com.example.insurance.domain.customerPolicy.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;
import com.example.insurance.common.enummuration.Relationship;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;
import com.example.insurance.domain.policyBeneficiary.model.PolicyBeneficiary;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.embeddable.BeneficiaryDetails;
import com.example.insurance.infrastructure.web.custommerPolicy.BeneficiaryDto;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.infrastructure.web.custommerPolicy.PaymentScheduleDto;
import com.example.insurance.shared.kernel.dtos.CategoryDto;

public class PolicyMapper {

    // Map BeneficiaryDto to PolicyBeneficiary entity

    public static PolicyBeneficiary mapToBeneficiaryEntity(BeneficiaryDto dto) {
        PolicyBeneficiary beneficiary = new PolicyBeneficiary();
        beneficiary.setDetails(mapBeneficiaryDetails(dto));
        return beneficiary;
    }

    // Map BeneficiaryDto to BeneficiaryDetails embeddable
    public static BeneficiaryDetails mapBeneficiaryDetails(BeneficiaryDto dto) {
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

    // Main method to map CustomerPolicy to InsurancePolicyDto
    public static InsurancePolicyDto toDto(CustomerPolicy policy) {
        if (policy == null)
            return null;

        InsurancePolicyDto dto = new InsurancePolicyDto();

        // Basic fields
        dto.setId(policy.getId());
        dto.setPolicyNumber(policy.getPolicyNumber());
        // dto.setPolicyNumber(policy.getProduct().getPolicyNumber());
        dto.setStatus(policy.getStatus());

        // Status change and cancellation fields
        dto.setStatusChangeNotes(policy.getStatusChangeNotes());
        dto.setCancellationReason(policy.getCancellationReason());
        dto.setCancellationDate(policy.getCancellationDate());
        dto.setCancelledBy(policy.getCancelledBy());

        // Financial fields
        if (policy.getPremium() != null) {
            dto.setPremium(policy.getPremium().getAmount());
            dto.setCurrency(policy.getPremium().getCurrency());
        }

        dto.setPaymentFrequency(policy.getPaymentFrequency());
        dto.setValidityPeriod(policy.getCoveragePeriod());

        // Map Product information if available
        dto.setProductCode(policy.getProduct().getProductCode());
        dto.setDisplayName(policy.getProduct().getDisplayName());
        dto.setDescription(policy.getProduct().getDescription());
        dto.setProductType(policy.getProduct().getProductType());
        dto.setCoverageDetails(policy.getProduct().getCoverageDetails());
        dto.setEligibilityRules(policy.getProduct().getEligibilityRules());
        dto.setTargetAudience(policy.getProduct().getTargetAudience());
        dto.setRegions(policy.getProduct().getRegion());
        dto.setTranslations(policy.getProduct().getTranslation());

        if (policy.getProduct() != null) {
            dto.setProductId(policy.getProduct().getId());
        }

        if (policy.getUser() != null) {
            dto.setUserId(policy.getUser().getUserId());
        }

        // Map Policy Holder information
        if (policy.getPolicyHolder() != null) {
            dto.setPolicyHolderName(
                    policy.getPolicyHolder().getName().getFirstName() + " " +
                            policy.getPolicyHolder().getName().getLastName());
            dto.setPolicyHolderEmail(policy.getPolicyHolder().getEmail());
            dto.setPolicyHolderId(policy.getPolicyHolder().getId()); // ADD THIS
        }

        dto.setBeneficiaries(
                policy.getBeneficiaries().stream().map((bene) -> mapPolicyBeneficiaryToDto(bene))
                        .collect(Collectors.toList()));

        // Convert Enums to Strings
        dto.setAllowedClaimTypes(
                policy.getProduct().getAllowedClaimTypes().stream().map(Enum::name).collect(Collectors.toSet()));

        // Map Category to DTO
        if (policy.getProduct().getCategory() != null) {
            dto.setCategory(new CategoryDto(
                    policy.getProduct().getCategory().getId(),
                    policy.getProduct().getCategory().getName(),
                    policy.getProduct().getCategory().getDescription()));
        }
        // Map payment schedules
        if (policy.getPaymentSchedules() != null) {
            dto.setPaymentSchedules(
                    policy.getPaymentSchedules().stream()
                            .map(PolicyMapper::mapToPaymentScheduleDto)
                            .collect(Collectors.toList()));
        }

        // Map Audit fields
        if (policy.getCreatedAt() != null) {
            dto.setCreatedAt(LocalDateTime.ofInstant(policy.getCreatedAt(), ZoneId.systemDefault()));
        }
        if (policy.getUpdatedAt() != null) {
            dto.setUpdatedAt(LocalDateTime.ofInstant(policy.getUpdatedAt(), ZoneId.systemDefault()));
        }
        dto.setCreatedBy(policy.getCreatedBy());
        dto.setUpdatedBy(policy.getUpdatedBy());

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

        dto.setStatus(paymentSchedule.getStatus().name());
        dto.setTransactionId(paymentSchedule.getTransactionId());

        String currentStatus = paymentSchedule.getStatus().name();
        if ("PENDING".equals(currentStatus)) {
            // Calculate overdue status for pending payments only
            if (paymentSchedule.getDueDate().isBefore(LocalDate.now())) {
                dto.setStatus("OVERDUE");
                ;
            }
        }

        // Map cancellation fields - ADD THESE
        dto.setCancellationDate(paymentSchedule.getCancellationDate());
        dto.setCancelledBy(paymentSchedule.getCancelledBy());

        return dto;
    }

}
