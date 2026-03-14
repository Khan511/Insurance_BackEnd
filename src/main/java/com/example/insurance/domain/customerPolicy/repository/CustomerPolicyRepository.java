package com.example.insurance.domain.customerPolicy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.shared.enummuration.PolicyStatus;

@Repository
public interface CustomerPolicyRepository extends JpaRepository<CustomerPolicy, Long> {
    List<CustomerPolicy> findByUser_UserId(String userId);

    Optional<CustomerPolicy> findByUser_UserIdAndId(String userId, Long policyId);

    List<CustomerPolicy> findByStatus(PolicyStatus status);

}
