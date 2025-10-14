package com.example.insurance.usecases.admin.service;

import java.util.List;

import com.example.insurance.infrastructure.web.custommerPolicy.InsurancePolicyDto;

public interface AdminService {

    List<InsurancePolicyDto> getAllPolicies();

}
