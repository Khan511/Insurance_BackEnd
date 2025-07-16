package com.example.insurance.infrastructure.web.insuranceProduct;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.service.InsuranceProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/policy")
public class InsuranceProductController {

    private final InsuranceProductService insuranceProductService;

    @GetMapping("/all-policies")
    public List<InsuraceProductDto> allPolicies() {
        return insuranceProductService.getAllPolicies();
    }

    @GetMapping("/policy-details/{policyId}")
    public InsuraceProductDto getPolictyById(@PathVariable Long policyId) {
        return insuranceProductService.getPolicyById(policyId);
    }

    // @PostMapping("/buy-policy")
    // public ResponseEntity<?> buyPolicy(@RequestBody BuyPolicyDto buyPolicyDto)
    // {

    // }

}
