package com.example.insurance.usecases.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.customerPolicy.repository.CustomerPolicyRepository;
import com.example.insurance.domain.customerPolicy.service.PolicyMapper;
import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final CustomerPolicyRepository customerPolicyRepository;

    @Override
    public List<InsurancePolicyDto> getAllPolicies() {

        System.out.println("Admin Service ===========================================>");

        List<CustomerPolicy> policies = customerPolicyRepository.findAll();

        return policies.stream().map(policy -> PolicyMapper.toDto(policy)).toList();

    }

}
