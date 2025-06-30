package com.example.insurance.domain.claimStatusHistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.insurance.domain.claimStatusHistory.model.ClaimStatusHistory;

@Repository
public interface ClaimStatusHistoryRepository extends JpaRepository<ClaimStatusHistory, Long> {

}
