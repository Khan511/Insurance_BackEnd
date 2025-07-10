package com.example.insurance.domain.insuranceCategory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.insurance.domain.insuranceCategory.model.InsuranceCategory;

public interface InsuranceCategoryRepository extends
        JpaRepository<InsuranceCategory, Long> {

    Optional<InsuranceCategory> findByName(String name);

}
