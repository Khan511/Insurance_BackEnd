package com.example.insurance.domain.insurancePolicy.service;

import java.util.List;
import com.example.insurance.domain.insurancePolicy.model.InsurancePolicy;
import com.example.insurance.usecases.policyCreation.model.CreateInsurancePolicy;

public interface InsurancePolicyServices {

    public List<CreateInsurancePolicy> getAllPolicies();
}
