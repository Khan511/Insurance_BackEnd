package com.example.insurance.infrastructure.web.custommerPolicy;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CustomerDto {
    // private String firstName;
    // private String lastName;
    // private String email;
    // private LocalDate dateOfBirth;
    private String userId;
    private GovernmentIdDto governmentId;
    private ContactInfoDto contactInfo;
}
