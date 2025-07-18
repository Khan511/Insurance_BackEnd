package com.example.insurance.infrastructure.web.custommerPolicy;

import java.time.LocalDate;

import com.example.insurance.domain.customer.model.GovernmentId;
import com.example.insurance.domain.customer.model.GovernmentId.VerificationStatus;

import lombok.Data;

@Data
public class GovernmentIdDto {

    private GovernmentId.IdType idType;
    private String idNumber;
    private String issuingCountry;
    private LocalDate expirationDate;
    private VerificationStatus verificationStatus;

}
