package com.example.insurance.embeddable;

import java.time.LocalDate;

import com.example.insurance.common.enummuration.Relationship;
// import com.example.insurance.domain.customer.model.GovernmentId;
// import com.example.insurance.domain.policyBeneficiary.model.TaxInformation;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
// import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class BeneficiaryDetails {
    @Column(name = "full_legal_name")
    private String fullLegalname;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Relationship relationship;

    // @Embedded
    // private GovernmentId governmentId;

    // @Column(name = "tax_info")
    // private TaxInformation taxInfo;

}
