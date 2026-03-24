package com.example.insurance.domain.insuranceProduct.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.insuranceProduct.repository.InsuranceProductRepository;
import com.example.insurance.shared.kernel.dtos.InsuraceProductDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsuraceProductServiceImpl implements InsuranceProductService {
    private final InsuranceProductRepository insuranceProductRepository;

    @Override
    public List<InsuraceProductDto> getAllPolicies() {

        List<InsuranceProduct> allProducts = insuranceProductRepository.findAll();
        return allProducts.stream().map(product -> ProductMapper.toDto(product)).toList();
    }

    @Override
    public InsuraceProductDto getPolicyById(Long policyId) {
        return ProductMapper.toDto(insuranceProductRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Product not found")));
    }

    @Override
    public InsuranceProduct getInsuranceProductByPolicyNumber(String policyNumber) {
        return insuranceProductRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new RuntimeException("Product not found"));

    }

}
