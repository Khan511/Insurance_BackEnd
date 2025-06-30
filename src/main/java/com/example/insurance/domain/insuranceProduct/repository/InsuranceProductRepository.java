package com.example.insurance.domain.insuranceProduct.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;

@Repository
public interface InsuranceProductRepository extends JpaRepository<InsuranceProduct, Long> {

}
