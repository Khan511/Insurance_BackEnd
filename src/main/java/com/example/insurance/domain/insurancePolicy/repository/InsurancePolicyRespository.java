package com.example.insurance.domain.insurancePolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
// import com.example.insurance.domain.insurancePolicy.model.InsurancePolicy;
import com.example.insurance.usecases.policyCreation.model.CreateInsurancePolicy;

public interface InsurancePolicyRespository extends JpaRepository<CreateInsurancePolicy, Long> {

}
