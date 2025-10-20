package com.example.insurance.usecases.admin.service;

import java.util.List;

import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.usecases.admin.controller.AdminPolicyRequestDto;

public interface AdminService {

    List<InsurancePolicyDto> getAllPolicies();

    List<ClaimResponseDTO> getAllClaims();

    void updatePolicy(AdminPolicyRequestDto dto);

}
