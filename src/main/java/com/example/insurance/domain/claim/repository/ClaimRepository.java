package com.example.insurance.domain.claim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.insurance.domain.claim.model.Claim;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

}
