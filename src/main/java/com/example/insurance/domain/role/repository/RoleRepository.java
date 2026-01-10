package com.example.insurance.domain.role.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.insurance.common.enummuration.RoleType;
import com.example.insurance.domain.role.model.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(RoleType roleType);

    // @Query("SELECT r FROM RoleEntity r JOIN r.users u WHERE u.id = :userId")
    // Set<RoleEntity> findRolesByUserId(@Param("userId") Long userId);

    // @Query("SELECT r FROM RoleEntity r JOIN FETCH r.permissions WHERE r.name =
    // :roleName")
    // Optional<RoleEntity> findByNameWithPermissions(@Param("roleName") String
    // roleName);
}