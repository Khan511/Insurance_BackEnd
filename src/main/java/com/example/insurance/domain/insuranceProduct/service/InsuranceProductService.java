package com.example.insurance.domain.insuranceProduct.service;

import java.util.List;

import com.example.insurance.infrastructure.web.insurancePolicy.BuyPolicyDto;
import com.example.insurance.infrastructure.web.insuranceProduct.InsuraceProductDto;

public interface InsuranceProductService {

    public List<InsuraceProductDto> getAllPolicies();

    public InsuraceProductDto getPolicyById(Long policyId);

}
