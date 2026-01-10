package com.example.insurance.shared.kernel.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Address {
    private String street;
    private String city;

    @Column(name = "postal_code")
    private String postalCode;
    private String country;
}
