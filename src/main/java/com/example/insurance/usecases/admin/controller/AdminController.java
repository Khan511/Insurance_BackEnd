package com.example.insurance.usecases.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        System.out.println("Admin Controller ===========================================>");
        List<InsurancePolicyDto> getAllPolicies = adminService.getAllPolicies();
        System.out.println("Admin polices ===========================================>" + getAllPolicies);

        return ResponseEntity.ok(getAllPolicies);
        // return ResponseEntity.ok().body(Map.of("policies", getAllPolicies));
    }

}
