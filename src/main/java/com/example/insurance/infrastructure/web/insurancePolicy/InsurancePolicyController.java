
package com.example.insurance.infrastructure.web.insurancePolicy;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.insurance.domain.insurancePolicy.model.InsurancePolicy;
import com.example.insurance.domain.insurancePolicy.service.InsurancePolicyServices;
import com.example.insurance.usecases.policyCreation.model.CreateInsurancePolicy;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/policy")
public class InsurancePolicyController {

    private final InsurancePolicyServices allPolicyServices;

    @GetMapping("/all-policies")
    public List<CreateInsurancePolicy> allPolicies() {

        return allPolicyServices.getAllPolicies();
    }
}
