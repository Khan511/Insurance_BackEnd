package com.example.insurance.domain.customer.model;

import java.time.LocalDate;
import java.util.List;
// import java.util.UUID;

import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
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

    @OneToMany(mappedBy = "policyHolder")
    private List<CustomerPolicy> policies;

}
