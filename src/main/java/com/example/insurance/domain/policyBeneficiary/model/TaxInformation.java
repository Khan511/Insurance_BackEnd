package com.example.insurance.domain.policyBeneficiary.model;

import com.example.insurance.common.enummuration.NordicCountry;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
@Embeddable
public class TaxInformation {

    @Enumerated(EnumType.STRING)
    private NordicCountry country;

    @Column(name = "tax_identifier")
    private String taxIdentifier; // Primary tax ID

    @Column(name = "dk_cpr")
    private String cprNumber; // PersonNummber (Denamrk)

    // @Column(name = "dk_tax_card_reference")
    private String taxCardReference; // Skattekort reference

    // Sweden-specific fields
    @Column(name = "se_personnummer")
    private String personnummer; // Sweden personal ID

    @Column(name = "se_skattekonto")
    private String skattekonto; // Tax account number

    // Norway-specific fields
    @Column(name = "no_fodselsnummer")
    private String fodselsnummer; // Birth number

    @Column(name = "no_tax_deduction_card")
    private String taxDeductionCard; // Trekkgrunnlagskort

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // Check if follwoing is neccessory

    // // EU tax status
    // @Column(name = "eu_tax_resident")
    // private boolean euTaxResident = true; // Default true for Nordic

    // @Column(name = "tax_treaty_benefits")
    // private boolean taxTreatyBenefits;

    // @Column(name = "tax_verification_doc_id")
    // private String taxVerificationDocId; // Ref

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

}
