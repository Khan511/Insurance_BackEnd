package com.example.insurance.domain.user.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.insurance.common.enummuration.RoleType;
import com.example.insurance.common.enummuration.UserStatus;
import com.example.insurance.domain.auditing.domain.AuditEntity;
import com.example.insurance.domain.role.model.RoleEntity;
import com.example.insurance.shared.kernel.embeddables.PersonName;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
public class User extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_uuid_id")
    private String userId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @JsonIgnore
    private String passwordHash;

    @Embedded
    private PersonName name;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = " login_attempts")
    private Integer loginAttempts;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    // Helper method
    public void addRole(RoleEntity roleEntity) {
        this.roles.add(roleEntity);
    }

    public void removeRole(RoleType roleType) {
        this.roles.remove(roleType);
    }

}
