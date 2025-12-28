package com.example.insurance.usecases.admin.service;

import java.util.List;

import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.usecases.admin.controller.AdminClaimUpdateRequest;
import com.example.insurance.usecases.admin.controller.AdminCustommersDto;
import com.example.insurance.usecases.admin.controller.AdminPolicyRequestDto;
import com.example.insurance.usecases.admin.controller.UpdateCustomerDto;

public interface AdminService {

    List<InsurancePolicyDto> getAllPolicies();

    List<ClaimResponseDTO> getAllClaims(ClaimSortRequest request);
    // List<ClaimResponseDTO> getAllClaims();

    void updatePolicy(AdminPolicyRequestDto dto);

    void updateClaim(AdminClaimUpdateRequest dto);

    PaymentSummaryDto getAllPayments();

    ClaimResponseDTO getClaimDetails(Long claimId);

    List<AdminCustommersDto> getAllCustomers();

    AdminCustommersDto getCustomerByUserId(String userId);

    AdminCustommersDto updateCustomer(UpdateCustomerDto dto);

    void approveClaim(Long claimId, ApproveClaimRequest request);

    void markClaimAsPaid(Long claimId, MarkAsPaidRequest request);

    void rejectClaim(Long claimId, RejectClaimRequest request);

}
