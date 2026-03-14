package com.example.insurance.domain.claim.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.shared.enummuration.ClaimStatus;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByUser_UserId(String userId);

    boolean existsByClaimNumber(String claimNumber);

    Optional<Claim> findByClaimNumber(String claimNumber);

    // Custom query with dynamic sorting
    @Query("SELECT c FROM Claim c")
    List<Claim> findAllWithSorting(Sort sort);

    List<Claim> findByPolicyNumberAndStatusIn(String policyNumber, List<ClaimStatus> status);

    List<Claim> findByPolicyNumberAndStatus(String policyNumber, ClaimStatus status);

}
