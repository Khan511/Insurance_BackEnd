package com.example.insurance.usecases.admin.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.insurance.common.enummuration.RoleType;
import com.example.insurance.domain.employee.domain.EmergencyContact;
import com.example.insurance.domain.employee.domain.WorkContactInfo;
import com.example.insurance.shared.kernel.embeddables.PersonName;

import lombok.Data;

@Data
public class EmployeeResponseDto {

    private Long id;
    private String employeeId;
    private String email;
    private PersonName name;
    private LocalDate dateOfBirth;
    private LocalDate terminationDate;
    private String department;
    private String jobTitle;
    private BigDecimal salary;
    private String employmentType;
    private RoleType roleType;
    private WorkContactInfo workContactInfo;
    private EmergencyContact emergencyContact;
    private LocalDate hireDate;
    private boolean isActive;
}
