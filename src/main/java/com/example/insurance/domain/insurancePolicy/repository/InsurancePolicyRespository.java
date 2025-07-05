package com.example.insurance.domain.insurancePolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.insurance.domain.insurancePolicy.model.InsurancePolicy;

public interface InsurancePolicyRespository extends JpaRepository<InsurancePolicy, Long> {

}
