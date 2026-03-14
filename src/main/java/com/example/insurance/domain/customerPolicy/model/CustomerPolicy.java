package com.example.insurance.domain.customerPolicy.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.insurance.domain.auditing.domain.AuditEntity;
import com.example.insurance.domain.customer.model.Customer;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;
import com.example.insurance.domain.policyBeneficiary.model.PolicyBeneficiary;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.shared.enummuration.PolicyStatus;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;
import com.example.insurance.shared.kernel.embeddables.PolicyPeriod;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    private Long id;

    @Column(name = "policy_number")
    private String policyNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // @Column(name = "user_id")
    // private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_holder_id")
    private Customer policyHolder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private InsuranceProduct product;

    // @Column(name = "product_id")
    // private Long productId;

    @Embedded
    private PolicyPeriod coveragePeriod;

    @Embedded
    private MonetaryAmount premium;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_frequency")
    private PaymentFrequency paymentFrequency;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "cancellation_date")
    private LocalDate cancellationDate;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    @Enumerated(EnumType.STRING)
    private PolicyStatus status;

    @Column(name = "status_change_notes")
    private String statusChangeNotes;

    @OneToMany(mappedBy = "customerPolicy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyBeneficiary> beneficiaries = new ArrayList<>();

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    private List<PaymentSchedule> paymentSchedules;

    // Helper methods
    public void addBeneficiary(PolicyBeneficiary policyBeneficiary) {
        beneficiaries.add(policyBeneficiary);
        policyBeneficiary.setCustomerPolicy(this);
    }

    public void removeBeneficiary(PolicyBeneficiary policyBeneficiary) {
        beneficiaries.remove(policyBeneficiary);
        policyBeneficiary.setCustomerPolicy(null);
    }

    // Helper methods for payment schedules
    public void addPaymentSchedule(PaymentSchedule paymentSchedule) {
        paymentSchedules.add(paymentSchedule);
        paymentSchedule.setPolicy(this);
    }

    public void removePaymentSchedule(PaymentSchedule paymentSchedule) {
        paymentSchedules.remove(paymentSchedule);
        paymentSchedule.setPolicy(null);
    }

}
