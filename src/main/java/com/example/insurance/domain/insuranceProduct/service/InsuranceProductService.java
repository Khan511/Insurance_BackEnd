package com.example.insurance.domain.insuranceProduct.service;

import java.util.List;

import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.shared.kernel.dtos.InsuraceProductDto;

public interface InsuranceProductService {

    public List<InsuraceProductDto> getAllPolicies();

    public InsuraceProductDto getPolicyById(Long policyId);

    public InsuranceProduct getInsuranceProductByPolicyNumber(String policyNumber);

}
