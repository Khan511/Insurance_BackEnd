package com.example.insurance.shared.kernel.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ThirdPartyDetails {

    private String name;
    private String contactInfo;

    @Column(name = "third_party_insurance_provider")
    private String insuranceInfo;

    @Column(name = "third_party_policy_number")
    private String policyNumber;
}
