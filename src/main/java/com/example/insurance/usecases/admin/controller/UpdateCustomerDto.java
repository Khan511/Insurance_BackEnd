package com.example.insurance.usecases.admin.controller;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class UpdateCustomerDto {

    String customerId;
    String customerFirstname;
    String customerLastname;
    String email;
    LocalDate customerDateOfBirth;
    String customerPhone;
    String customerAlternativePhone;
    String customerPrimaryAddressStreet;
    String customerPrimaryAddressCity;
    String customerPrimaryAddressPostalCode;
    String customerPrimaryAddressCountry;
    String customerBillingAddressStreet;
    String customerBillingAddressCity;
    String customerBillingAddressPostalCode;
    String customerBillingAddressCountry;
    // String status;
}
