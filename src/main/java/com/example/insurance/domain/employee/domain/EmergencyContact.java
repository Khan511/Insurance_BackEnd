package com.example.insurance.domain.employee.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContact {

    @Column(name = "emergency_contact_name")
    private String name;

    @Column(name = "emergency_contact_relationship")
    private String relationship;

    @Column(name = "emergency_contact_phone")
    private String phone;

    @Column(name = "emergency_contact_email")
    private String email;
}
