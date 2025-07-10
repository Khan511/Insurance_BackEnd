package com.example.insurance.domain.customer.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
// import java.util.UUID;

import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.user.model.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "customers")
public class Customer extends User {
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long id;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // Passport/SSN
    @Embedded
    private GovernmentId governmentId;

    @Embedded
    private ContactInfo contactInfo;

    @OneToMany(mappedBy = "policyHolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerPolicy> policies = new ArrayList<>();

    // Helper method to add policy
    public void addPolicy(CustomerPolicy policy) {
        policies.add(policy);
        policy.setPolicyHolder(this);
    }

}
