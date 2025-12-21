package com.example.insurance.usecases.admin.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.insurance.infrastructure.web.claim.ClaimResponseDTO;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;
import com.example.insurance.usecases.admin.service.AdminService;
import com.example.insurance.usecases.admin.service.ApproveClaimRequest;
import com.example.insurance.usecases.admin.service.MarkAsPaidRequest;
import com.example.insurance.usecases.admin.service.PaymentSummaryDto;
import com.example.insurance.usecases.admin.service.RejectClaimRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    @PutMapping("/update-claim")
    public ResponseEntity<?> updateClaim(@RequestBody AdminClaimUpdateRequest request) {

        adminService.updateClaim(request);

        return ResponseEntity.ok(Map.of("message", "Claim updated successfully!"));
    }

    @GetMapping("/get-all-payments")
    public ResponseEntity<PaymentSummaryDto> getAllPayments() {

        return ResponseEntity.ok(adminService.getAllPayments());
    }

    @GetMapping("/get-all-customers")
    public ResponseEntity<?> getAllCustomers() {

        List<AdminCustommersDto> getAllCustomers = adminService.getAllCustomers();

        return ResponseEntity.ok().body(getAllCustomers);
    }

    @GetMapping("/get-customer/{customerId}")
    public ResponseEntity<?> getCustomer(@PathVariable String customerId) {

        AdminCustommersDto getCustomerByUserId = adminService.getCustomerByUserId(customerId);

        return ResponseEntity.ok().body(getCustomerByUserId);
    }

    @PatchMapping("/update-customers")
    public ResponseEntity<?> updateCustomer(@RequestBody UpdateCustomerDto dto) {

        AdminCustommersDto upatedCustomer = adminService.updateCustomer(dto);
        return ResponseEntity.ok(upatedCustomer);
    }

    @PatchMapping("/claims/{claimId}/approve")
    public ResponseEntity<?> approveClaim(@PathVariable Long claimId,
            @RequestBody ApproveClaimRequest request) {

        try {
            adminService.approveClaim(claimId, request);

            return ResponseEntity.ok(Map.of("message", "Claim approved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("=======================Error approving claim {}: {}", claimId, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to approve claim"));
        }
    }

    @PatchMapping("/claims/{claimId}/reject")
    public ResponseEntity<?> rejectClaim(
            @PathVariable Long claimId,
            @RequestBody RejectClaimRequest request) {

        try {
            adminService.rejectClaim(claimId, request);

            return ResponseEntity.ok(Map.of(
                    "message", "Claim rejected successfully!",
                    "claimId", claimId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error rejecting claim {}: {}", claimId, e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to reject claim"));
        }
    }

    @PatchMapping("/claims/{claimId}/mark-paid")
    public ResponseEntity<?> markClaimAsPaid(
            @PathVariable Long claimId,
            @RequestBody MarkAsPaidRequest request) {

        adminService.markClaimAsPaid(claimId, request);

        return ResponseEntity.ok(Map.of(
                "message", "Claim marked as paid successfully!",
                "claimId", claimId));
    }

}
