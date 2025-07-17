package com.example.insurance.domain.customerPolicy.service;

import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.infrastructure.web.insurancePolicy.BuyPolicyDto;

public interface CustomerPolicyService {

    public void saveCustomerPolicy(BuyPolicyDto buyPolicyDto);
}
