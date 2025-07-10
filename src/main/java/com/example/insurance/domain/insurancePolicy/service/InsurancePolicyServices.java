package com.example.insurance.domain.insurancePolicy.service;

import java.util.List;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;

public interface InsurancePolicyServices {

    public List<InsuranceProduct> getAllPolicies();

    public InsuranceProduct getPolicyById(long id);
}
