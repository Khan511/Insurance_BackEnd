package com.example.insurance.domain.policyBeneficiary.model;

import com.example.insurance.domain.auditing.domain.AuditEntity;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.embeddable.BeneficiaryDetails;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "policy_beneficiaries")
public class PolicyBeneficiary extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(name = "details")
    @Embedded
    private BeneficiaryDetails details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private CustomerPolicy customerPolicy;

}
