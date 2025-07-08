
package com.example.insurance.infrastructure.web.insurancePolicy;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.insurance.domain.insurancePolicy.model.InsurancePolicy;
import com.example.insurance.domain.insurancePolicy.service.InsurancePolicyServices;
import com.example.insurance.usecases.policyCreation.model.CreateInsurancePolicy;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/policy")
public class InsurancePolicyController {

    private final InsurancePolicyServices insurancePolicyServices;

    @GetMapping("/all-policies")
    public List<CreateInsurancePolicy> allPolicies() {
        return insurancePolicyServices.getAllPolicies();
    }

    @GetMapping("/policy-details/{policyId}")
    public CreateInsurancePolicy getPolictyById(@PathVariable Long policyId) {
        System.out.println("Policy Id: ==============================>" + policyId);
        return insurancePolicyServices.getPolicyById(policyId);
    }

}
