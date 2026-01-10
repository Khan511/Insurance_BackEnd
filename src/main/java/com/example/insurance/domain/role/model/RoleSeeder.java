
// package com.example.insurance.domain.role.model;

// import java.util.Map;
// import java.util.Set;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;
// import com.example.insurance.common.enummuration.RoleType;
// import com.example.insurance.domain.role.repository.RoleRepository;
// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class RoleSeeder implements CommandLineRunner {

// private final RoleRepository roleRepository;

// @Override
// public void run(String... args) {

// Map<RoleType, Set<Permission>> rolePermission = Map.of(
// // CUSTOMER - Basic customer functions
// RoleType.CUSTOMER, Set.of(
// Permission.VIEW_POLICY,
// Permission.FILE_CLAIM,
// Permission.VIEW_CLAIM,
// Permission.MAKE_PAYMENT,
// Permission.VIEW_USER),

// // AGENT - Sales and customer support
// RoleType.AGENT, Set.of(
// Permission.CREATE_POLICY,
// Permission.EDIT_POLICY,
// Permission.VIEW_POLICY,
// Permission.VIEW_USER,
// Permission.VIEW_CLAIM,
// Permission.FILE_CLAIM,
// Permission.MAKE_PAYMENT,
// Permission.CALCULATE_PREMIUM,
// Permission.VIEW_PAYMENT_HISTORY),

// // CLAIM_MANAGER - Handle claims
// RoleType.CLAIM_MANAGER, Set.of(
// Permission.VIEW_CLAIM,
// Permission.EDIT_CLAIM,
// Permission.APPROVE_CLAIM,
// Permission.REJECT_CLAIM,
// Permission.VIEW_POLICY,
// Permission.VIEW_USER,
// Permission.VIEW_PAYMENT_HISTORY,
// Permission.VIEW_AUDIT_LOGS),

// // ADMIN - Full system access
// RoleType.ADMIN, Set.of(Permission.values()));

// rolePermission.forEach((roleType, perms) -> {
// roleRepository.findByName(roleType).ifPresentOrElse(
// role -> updatePermissions(role, perms),
// () -> createRole(roleType, perms));
// });
// }

// private void createRole(RoleType roleType, Set<Permission> permissions) {
// RoleEntity role = new RoleEntity();
// role.setName(roleType);
// permissions.forEach(role::addPermission);
// roleRepository.save(role);
// }

// private void updatePermissions(RoleEntity role, Set<Permission>
// newPermissions) {
// if (!role.getPermissions().equals(newPermissions)) {
// role.setPermissions(newPermissions);
// roleRepository.save(role);
// }
// }
// }