package com.example.insurance.domain.customerPolicy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;

@Repository
public interface CustomerPolicyRepository extends JpaRepository<CustomerPolicy, Long> {
    List<CustomerPolicy> findByUser_UserId(String userId);

}
