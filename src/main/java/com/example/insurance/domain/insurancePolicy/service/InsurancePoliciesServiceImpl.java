package com.example.insurance.domain.insurancePolicy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.insurance.domain.insurancePolicy.model.InsurancePolicy;
import com.example.insurance.domain.insurancePolicy.repository.InsurancePolicyRespository;
import com.example.insurance.usecases.policyCreation.model.CreateInsurancePolicy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsurancePoliciesServiceImpl implements InsurancePolicyServices {

    private final InsurancePolicyRespository insurancePolicyRespository;

    @Override
    public List<CreateInsurancePolicy> getAllPolicies() {
        List<CreateInsurancePolicy> allPolicies = insurancePolicyRespository.findAll();

        System.out.println(allPolicies);
        return allPolicies;
    }

}
