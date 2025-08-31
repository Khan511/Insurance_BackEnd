package com.example.insurance.domain.claimDocuments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.insurance.domain.claimDocuments.model.ClaimDocuments;

public interface ClaimDocumentsRepository extends JpaRepository<ClaimDocuments, Long> {

}
