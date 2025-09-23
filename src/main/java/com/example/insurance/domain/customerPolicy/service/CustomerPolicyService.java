package com.example.insurance.domain.customerPolicy.service;

import java.time.LocalDate;
import java.util.List;

import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.infrastructure.web.custommerPolicy.BuyPolicyDto;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;

public interface CustomerPolicyService {

    public void saveCustomerPolicy(BuyPolicyDto buyPolicyDto);

    public List<InsurancePolicyDto> getAllPoliciesOfUser(String userId);

    public InsurancePolicyDto getInsuranceDetails(String policyId, String userId);

    public List<CustomerPolicy> findByUserId(String userId);

    // int calculateAge(LocalDate birthDate);

}
