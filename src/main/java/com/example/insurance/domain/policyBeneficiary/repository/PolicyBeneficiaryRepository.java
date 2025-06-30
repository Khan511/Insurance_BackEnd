package com.example.insurance.domain.policyBeneficiary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.insurance.domain.policyBeneficiary.model.PolicyBeneficiary;

@Repository
public interface PolicyBeneficiaryRepository extends JpaRepository<PolicyBeneficiary, Long> {

}
