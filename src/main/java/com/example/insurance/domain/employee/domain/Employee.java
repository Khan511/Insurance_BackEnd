package com.example.insurance.domain.employee.domain;

import com.example.insurance.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "employees")
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Employee extends User {

    // Employee-specific fields
    @Column(name = "employee_id", unique = true)
    private String employeeId;
    @Column(name = "department")
    private String department;

    @Column(name = "job_title")
    private String jobTitle;
    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "salary")
    private BigDecimal salary;

    // FULL_TIME, PART_TIME, CONTRACT
    @Column(name = "employment_type")
    private String employmentType;

    @Embedded
    private WorkContactInfo workContactInfo;

    @Embedded
    private EmergencyContact emergencyContact;

    @Column(name = "is_active")
    private boolean isActive = true;

    // Helper method to generate employee ID
    public void generateEmployeeId(String prefix) {
        if (this.employeeId == null) {
            // This would typically come from a sequence in real scenario
            this.employeeId = prefix + String.format("%03d", this.getId());
        }
    }
}