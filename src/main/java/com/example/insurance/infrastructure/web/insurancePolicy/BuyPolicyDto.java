package com.example.insurance.infrastructure.web.insurancePolicy;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BuyPolicyDto {

    private String firstName;
    private String lastName;
    private String email;

    private LocalDate dateOfBirth;
    private GovernmentIdDto governmentIdDto;
    private ContactInfoDto contactInfoDto;
    private CoveragePeriodDto conCoveragePeriodDto;
    private PremiumDto premiumDto;
    private BeneficiaryDto beneficiaryDto;

};
