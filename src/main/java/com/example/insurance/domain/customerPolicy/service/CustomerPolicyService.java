package com.example.insurance.domain.customerPolicy.service;

import java.util.List;

import com.example.insurance.infrastructure.web.custommerPolicy.BuyPolicyDto;
import com.example.insurance.shared.kernel.dtos.InsuraceProductDto;

public interface CustomerPolicyService {

    public void saveCustomerPolicy(BuyPolicyDto buyPolicyDto);

    public List<InsuraceProductDto> getAllPoliciesOfUser(String userId);

}
