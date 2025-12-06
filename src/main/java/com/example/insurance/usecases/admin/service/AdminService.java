package com.example.insurance.usecases.admin.service;

import java.util.List;

import com.example.insurance.domain.customer.model.Customer;
import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.usecases.admin.controller.AdminClaimUpdateRequest;
import com.example.insurance.usecases.admin.controller.AdminCustommersDto;
import com.example.insurance.usecases.admin.controller.AdminPolicyRequestDto;

public interface AdminService {

    List<InsurancePolicyDto> getAllPolicies();

    List<ClaimResponseDTO> getAllClaims();

    void updatePolicy(AdminPolicyRequestDto dto);

    void updateClaim(AdminClaimUpdateRequest dto);

    PaymentSummaryDto getAllPayments();

    List<AdminCustommersDto> getAllCustomers();

    AdminCustommersDto getCustomerByUserId(String userId);
}
