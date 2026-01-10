package com.example.insurance.usecases.admin.employee;

import lombok.Data;
import java.time.LocalDate;
import java.math.BigDecimal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.example.insurance.common.enummuration.RoleType;
import com.example.insurance.shared.kernel.embeddables.PersonName;
import com.example.insurance.domain.employee.domain.WorkContactInfo;
import com.example.insurance.domain.employee.domain.EmergencyContact;

@Data
public class EmployeeUpdateDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Length(min = 8, message = "Password must be at least  characters")
    private String password;

    @NotNull(message = "Name is required")
    private PersonName name;

    private LocalDate dateOfBirth;
    private LocalDate terminationDate;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @NotNull(message = "Salary is required")
    private BigDecimal salary;

    @NotBlank(message = "Employment type is required")
    private String employmentType;

    @NotNull(message = "Role type is required")
    private RoleType roleType;

    private WorkContactInfo workContactInfo;
    private EmergencyContact emergencyContact;
}