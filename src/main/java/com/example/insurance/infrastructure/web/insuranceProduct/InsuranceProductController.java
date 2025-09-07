package com.example.insurance.infrastructure.web.insuranceProduct;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.insurance.domain.insuranceProduct.service.InsuranceProductService;
import com.example.insurance.shared.kernel.dtos.InsuraceProductDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class InsuranceProductController {

    private final InsuranceProductService insuranceProductService;

    @GetMapping("/all-products")
    public List<InsuraceProductDto> allPolicies() {
        return insuranceProductService.getAllPolicies();
    }

    @GetMapping("/product-details/{policyId}")
    public InsuraceProductDto getPolictyById(@PathVariable Long policyId) {
        return insuranceProductService.getPolicyById(policyId);
    }
}
