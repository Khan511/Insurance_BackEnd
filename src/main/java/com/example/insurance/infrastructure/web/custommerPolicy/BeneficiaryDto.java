package com.example.insurance.infrastructure.web.custommerPolicy;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BeneficiaryDto {
    private String name;
    private String relationship;
    private LocalDate dateOfBirth;

}
