package com.example.insurance.domain.insuranceProduct.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;

@Repository
public interface InsuranceProductRepository extends JpaRepository<InsuranceProduct, Long> {

    Optional<InsuranceProduct> findById(Long policyId);

    InsuranceProduct findByPolicyNumber(String policyNumber);

}
