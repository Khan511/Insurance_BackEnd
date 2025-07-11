// package com.example.insurance.domain.insurancePolicy.service;

// import java.util.List;
// import org.springframework.stereotype.Service;
// import
// com.example.insurance.domain.insurancePolicy.repository.InsurancePolicyRespository;
// import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class InsurancePoliciesServiceImpl implements InsurancePolicyServices
// {

// private final InsurancePolicyRespository insurancePolicyRespository;

// @Override
// public List<InsuranceProduct> getAllPolicies() {
// List<InsuranceProduct> allPolicies = insurancePolicyRespository.findAll();
// return allPolicies;
// }

// @Override
// public InsuranceProduct getPolicyById(long id) {
// return insurancePolicyRespository.findById(id).orElseThrow(() -> new
// RuntimeException("Policy not found"));
// }

// }
