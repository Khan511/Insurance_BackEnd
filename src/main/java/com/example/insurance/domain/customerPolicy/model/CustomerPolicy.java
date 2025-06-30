package com.example.insurance.domain.customerPolicy.model;

import java.util.List;
// import java.util.UUID;

import com.example.insurance.common.enummuration.PolicyStatus;
import com.example.insurance.domain.auditing.domain.AuditEntity;
import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.domain.customer.model.Customer;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;
import com.example.insurance.domain.policyBeneficiary.model.PolicyBeneficiary;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;
import com.example.insurance.shared.kernel.embeddables.PolicyPeriod;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "customer_policies")
public class CustomerPolicy extends AuditEntity {
    // Custom generated ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer policyHolder;

    @ManyToOne(fetch = FetchType.LAZY)
    private InsuranceProduct product;

    @Embedded
    private PolicyPeriod coveragePeriod;

    @Embedded
    private MonetaryAmount premium;

    @Enumerated(EnumType.STRING)
    private PolicyStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyBeneficiary> beneficiaries;

    @OneToMany(mappedBy = "policy")
    private List<Claim> claims;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PaymentSchedule> paymentSchedules;

}
