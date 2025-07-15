package com.example.insurance.domain.policyBeneficiary.model;

import com.example.insurance.domain.auditing.domain.AuditEntity;
import com.example.insurance.embeddable.BeneficiaryDetails;
// import com.example.insurance.embeddable.TaxIdentifierValidator;
// import jakarta.persistence.AttributeOverride;
// import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
// import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "policy_beneficiaries")
public class PolicyBeneficiary extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "details")
    private BeneficiaryDetails details;

    // I CAN SET IT OP LATER IF I WANT TO:
    // @Embedded
    // @AttributeOverrides({
    // @AttributeOverride(name = "country", column = @Column(name =
    // "beneficiary_tax_country")),
    // @AttributeOverride(name = "taxIdentifier", column = @Column(name =
    // "beneficiary_tax_id")),
    // @AttributeOverride(name = "cprNumber", column = @Column(name =
    // "beneficiary_dk_cpr")),
    // @AttributeOverride(name = "taxCardReference", column = @Column(name =
    // "beneficiary_dk_tax_card")),
    // @AttributeOverride(name = "personnummer", column = @Column(name =
    // "beneficiary_se_personnummer")),
    // @AttributeOverride(name = "skattekonto", column = @Column(name =
    // "beneficiary_se_skattekonto")),
    // @AttributeOverride(name = "fodselsnummer", column = @Column(name =
    // "beneficiary_no_fodselsnummer")),
    // @AttributeOverride(name = "taxDeductionCard", column = @Column(name =
    // "beneficiary_no_tax_card"))
    // })
    // private TaxInformation taxInformation;

    // // Business Logic
    // public void validateTaxInformation() {
    // if (!TaxIdentifierValidator.isValidTaxId(taxInformation.getCountry(),
    // taxInformation.getTaxIdentifier())) {
    // throw new RuntimeException("Invalid tax ID format for " +
    // taxInformation.getCountry());
    // }
    // }

}
