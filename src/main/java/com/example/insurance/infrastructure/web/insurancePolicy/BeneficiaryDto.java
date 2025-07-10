package com.example.insurance.infrastructure.web.insurancePolicy;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BeneficiaryDto {
    private String name;
    private String relationship;
    private LocalDate dateOfBirth;
    private String taxCountry;
    private String tasIdentifier;

}
