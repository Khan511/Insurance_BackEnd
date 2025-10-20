package com.example.insurance.usecases.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.shared.kernel.dtos.InsuraceProductDto;
import com.example.insurance.usecases.admin.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/get-all-policies")
    public ResponseEntity<?> getAllPolicies() {
        List<InsurancePolicyDto> getAllPolicies = adminService.getAllPolicies();

        return ResponseEntity.ok(getAllPolicies);
    }

    @GetMapping("/get-all-claims")
    public ResponseEntity<?> getAllClaims() {
        List<ClaimResponseDTO> getAllClaims = adminService.getAllClaims();

        return ResponseEntity.ok(getAllClaims);
    }

    @PutMapping("/update-policy")
    public ResponseEntity<?> updatePolicy(@RequestBody AdminPolicyRequestDto dto) {
        adminService.updatePolicy(dto);

        return ResponseEntity.ok(Map.of("message", "Policy updated successfully!"));
    }

}
