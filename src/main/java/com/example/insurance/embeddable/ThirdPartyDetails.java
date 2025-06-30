package com.example.insurance.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ThirdPartyDetails {

    private String contactName;
    private String contactPhone;

    @Column(name = "third_party_insurance_provider")
    private String insuranceProvider;

    @Column(name = "third_party_policy_number")
    private String policyNumber;
}
