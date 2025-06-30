package com.example.insurance.domain.role.model;

import java.util.Map;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.insurance.common.enummuration.RoleType;
import com.example.insurance.domain.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

        private final RoleRepository roleRepository;

        @Override
        public void run(String... args) {

                Map<RoleType, Set<Permission>> rolePermission = Map.of(
                                RoleType.USER, Set.of(),
                                RoleType.ADMIN, Set.of(Permission.values()),
                                RoleType.CUSTOMER, Set.of(
                                                Permission.VIEW_POLICY, Permission.MAKE_PAYMENT,
                                                Permission.FILE_CLAIM,
                                                Permission.VIEW_CLAIM),
                                RoleType.AGENT, Set.of(
                                                Permission.CREATE_POLICY, Permission.EDIT_POLICY,
                                                Permission.VIEW_USER),
                                RoleType.UNDERWRITER, Set.of(
                                                Permission.APPROVE_POLICY, Permission.CALCULATE_PREMIUM),
                                RoleType.CLAIM_MANAGER, Set.of(
                                                Permission.VIEW_CLAIM, Permission.APPROVE_CLAIM,
                                                Permission.REJECT_CLAIM, Permission.EDIT_CLAIM),
                                RoleType.CUSTOMER_SUPPORT, Set.of(
                                                Permission.VIEW_USER, Permission.VIEW_POLICY,
                                                Permission.VIEW_CLAIM),
                                RoleType.AUDITOR, Set.of(
                                                Permission.VIEW_AUDIT_LOGS, Permission.EXPORT_LOGS),
                                RoleType.PARTNER, Set.of(
                                                Permission.VIEW_POLICY, Permission.CREATE_POLICY));

                rolePermission.forEach((roleType, perms) -> {

                        roleRepository.findByName(roleType).ifPresentOrElse(
                                        role -> updaterPermissions(role, perms),
                                        () -> createRole(roleType, perms));
                });
        }

        private void createRole(RoleType roleType, Set<Permission> permissions) {
                RoleEntity role = new RoleEntity();
                role.setName(roleType);
                permissions.forEach(role::addPermission);
                roleRepository.save(role);
        }

        private void updaterPermissions(RoleEntity role, Set<Permission> newPermissions) {
                if (!role.getPermissions().equals(newPermissions)) {
                        role.setPermissions(newPermissions);
                        roleRepository.save(role);
                }
        }
}