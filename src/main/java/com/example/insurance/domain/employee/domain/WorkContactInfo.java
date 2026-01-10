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
public class WorkContactInfo {

    @Column(name = "work_phone")
    private String workPhone;

    @Column(name = "work_email")
    private String workEmail;

    @Column(name = "office_location")
    private String officeLocation;

}
