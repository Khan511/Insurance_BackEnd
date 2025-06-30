package com.example.insurance.shared.kernel.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    private String street;
    private String city;
    private String state;

    @Column(name = "postal_code")
    private String postalCode;
    private String country;
}
