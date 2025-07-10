package com.example.insurance.infrastructure.web.insurancePolicy;

import lombok.Data;

@Data
public class ContactInfoDto {

    private String phone;
    private String alternatePhone;
    private AddressDto primaryAddress;
    private AddressDto billingAddress;
}
