package com.example.insurance.domain.role.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.example.insurance.domain.role.model.RoleEntity;
import com.example.insurance.shared.enummuration.RoleType;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(RoleType roleType);

}