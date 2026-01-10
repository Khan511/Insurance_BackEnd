package com.example.insurance.usecases.admin.employee;

import com.example.insurance.common.enummuration.RoleType;
import com.example.insurance.domain.employee.domain.EmergencyContact;
import com.example.insurance.domain.employee.domain.WorkContactInfo;
import com.example.insurance.shared.kernel.embeddables.PersonName;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeCreateDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotNull
    private PersonName name;

    private LocalDate dateOfBirth;

    private LocalDate terminationDate;

    @NotBlank
    private String department;

    @NotBlank
    private String jobTitle;

    @NotNull
    @Positive
    private BigDecimal salary;

    @NotBlank
    private String employmentType;

    @NotNull(message = "Role is required for employees")
    private RoleType roleType;

    private WorkContactInfo workContactInfo;

    private EmergencyContact emergencyContact;
}
