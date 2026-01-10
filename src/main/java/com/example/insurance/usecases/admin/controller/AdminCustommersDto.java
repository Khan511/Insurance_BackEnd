package com.example.insurance.usecases.admin.controller;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminCustommersDto {

    String customerId;
    String customerFirstname;
    String customerLastname;
    String email;
    String joinDate;
    String customerDateOfBirth;

    Integer numberOfPolicies;
    BigDecimal premium;
    String currency;
    String status;
    Long customerActivePolicies;

    String customerPhone;
    String customerAlternativePhone;

    String customerIdType;
    String customerIdMaskedNumber;
    String IdIssuingCountry;
    String IdExpirationDate;
    String IdVerificationStatus;

    String role;

    String customerPrimaryAddressStreet;
    String customerPrimaryAddressCity;
    String customerPrimaryAddressPostalCode;
    String customerPrimaryAddressCountry;

    String customerBillingAddressStreet;
    String customerBillingAddressCity;
    String customerBillingAddressPostalCode;
    String customerBillingAddressCountry;

}
