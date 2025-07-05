package com.example.insurance.domain.insuranceCategory.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.insurance.domain.insurancePolicy.model.InsurancePolicy;
import com.example.insurance.domain.insurancePolicy.repository.InsurancePolicyRespository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuranceCategoryServiceImpl implements InsuranceCategoryServices {

    private final InsurancePolicyRespository allPoliciesRespository;

    @Override
    public List<InsurancePolicy> getAllPolicies() {
        return allPoliciesRespository.findAll();
    }

}
