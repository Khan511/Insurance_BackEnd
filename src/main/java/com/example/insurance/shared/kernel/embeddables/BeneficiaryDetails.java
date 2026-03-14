package com.example.insurance.shared.kernel.embeddables;

import java.time.LocalDate;

import com.example.insurance.shared.enummuration.Relationship;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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

}
