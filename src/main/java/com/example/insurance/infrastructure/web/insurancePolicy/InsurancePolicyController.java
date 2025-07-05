
package com.example.insurance.infrastructure.web.insurancePolicy;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.insurance.domain.insurancePolicy.model.InsurancePolicy;
import com.example.insurance.domain.insurancePolicy.service.InsurancePolicyServices;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/policy")
public class InsurancePolicyController {

    private final InsurancePolicyServices allPolicyServices;

    @GetMapping("/all-policies")
    public List<InsurancePolicy> allPolicies() {

        return allPolicyServices.getAllPolicies();
    }
}
