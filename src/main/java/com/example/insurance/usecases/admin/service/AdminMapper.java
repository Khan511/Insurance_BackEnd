package com.example.insurance.usecases.admin.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.insurance.common.enummuration.PolicyStatus;
import com.example.insurance.domain.customer.model.Customer;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.usecases.admin.controller.AdminCustommersDto;

public class AdminMapper {

    public static AdminCustommersDto toAdminCustomerDto(Customer customer) {
        AdminCustommersDto dto = new AdminCustommersDto();

        dto.setCustomerId(customer.getUserId());
        dto.setCustomerFirstname(customer.getName().getFirstName());
        dto.setCustomerLastname(customer.getName().getLastName());
        dto.setEmail(customer.getEmail());
        if (customer.getCreatedAt() != null) {
            dto.setJoinDate(customer.getCreatedAt().toString());
        }
        dto.setCustomerDateOfBirth(customer.getDateOfBirth().toString());

        // Total policies
        List<CustomerPolicy> policies = customer.getPolicies();
        dto.setNumberOfPolicies(policies != null ? policies.size() : 0);

        // Calculate total premimu and get currency and status
        if (policies != null && !policies.isEmpty()) {
            BigDecimal totalPremium = policies.stream()
                    .map(policy -> policy.getPremium() != null ? policy.getPremium().getAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // User currency from the first policy(assume same currency for all)
            String currency = policies.get(0).getPremium() != null ? policies.get(0).getPremium().getCurrency() : "DKK";

            dto.setPremium(totalPremium);
            dto.setCurrency(currency);

            // Set status based on active polices
            boolean hasActivePolices = policies.stream()
                    .anyMatch(policy -> policy.getStatus() != null && policy.getStatus().name().equals("ACTIVE"));

            dto.setStatus(hasActivePolices ? "ACTIVE" : "INACTIVE");

        } else {
            dto.setPremium(BigDecimal.ZERO);
            dto.setCurrency("DKK");
            dto.setStatus("NO_POLICIES");
        }

        // Active Policies
        Long activePolicies = policies == null ? 0
                : policies.stream().filter(policy -> policy.getStatus() == PolicyStatus.ACTIVE).count();
        dto.setCustomerActivePolicies(activePolicies);

        dto.setCustomerPhone(customer.getContactInfo().getPhone());

        if (customer.getContactInfo().getAlternatePhone() != null) {
            dto.setCustomerAlternativePhone(customer.getContactInfo().getAlternatePhone());
        }

        dto.setCustomerIdType(customer.getGovernmentId().getIdType().name());
        dto.setCustomerIdMaskedNumber(customer.getGovernmentId().getMaskedNumber());
        dto.setIdIssuingCountry(customer.getGovernmentId().getIssuingCountry());
        dto.setIdExpirationDate(customer.getGovernmentId().getExpirationDate().toString());
        dto.setIdVerificationStatus(customer.getGovernmentId().getVerificationStatus().name());

        dto.setCustomerPrimaryAddressStreet(customer.getContactInfo().getPrimaryAddress().getStreet());
        dto.setCustomerPrimaryAddressCity(customer.getContactInfo().getPrimaryAddress().getCity());
        dto.setCustomerPrimaryAddressPostalCode(customer.getContactInfo().getPrimaryAddress().getPostalCode());
        dto.setCustomerPrimaryAddressCountry(customer.getContactInfo().getPrimaryAddress().getCountry());

        dto.setCustomerBillingAddressStreet(customer.getContactInfo().getBillingAddress().getStreet());
        dto.setCustomerBillingAddressCity(customer.getContactInfo().getBillingAddress().getCity());
        dto.setCustomerBillingAddressPostalCode(customer.getContactInfo().getBillingAddress().getPostalCode());
        dto.setCustomerBillingAddressCountry(customer.getContactInfo().getBillingAddress().getCountry());

        return dto;
    }

    public static String mapSortField(String requestField) {

        return "createdAt";
    }
}
