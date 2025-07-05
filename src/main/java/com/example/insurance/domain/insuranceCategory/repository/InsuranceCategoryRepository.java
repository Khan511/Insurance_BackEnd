package com.example.insurance.domain.insuranceCategory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.insurance.domain.insurancePolicy.model.InsurancePolicy;

public interface InsuranceCategoryRepository extends JpaRepository<InsurancePolicy, Long> {

}
