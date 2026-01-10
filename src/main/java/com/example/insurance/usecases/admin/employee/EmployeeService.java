
package com.example.insurance.usecases.admin.employee;

import com.example.insurance.common.enummuration.RoleType;
import com.example.insurance.common.enummuration.UserStatus;
import com.example.insurance.domain.employee.domain.Employee;
import com.example.insurance.domain.employee.repository.EmployeeRepository;
import com.example.insurance.domain.role.model.RoleEntity;
import com.example.insurance.domain.role.repository.RoleRepository;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.service.UserService;
import com.example.insurance.shared.kernel.embeddables.PersonName;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    // CREATE
    @Transactional
    public Employee createEmployee(EmployeeCreateDto dto, String userName) {
        // Check if email already exists
        if (employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Employee with this email already exists");
        }

        // Validate employee role
        validateEmployeeRole(dto.getRoleType());

        // Create Employee entity
        Employee employee = new Employee();

        // Set User fields
        employee.setUserId(UUID.randomUUID().toString());
        employee.setEmail(dto.getEmail());
        employee.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        employee.setName(dto.getName());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setEnabled(true);
        employee.setCreatedBy(userName);
        employee.setCreatedAt(Instant.now());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setEmailVerifiedAt(LocalDateTime.now());
        employee.setStatus(UserStatus.ACTIVE);
        employee.setLastLogin(LocalDateTime.now());

        // Set Employee-specific fields
        employee.setEmployeeId(generateEmployeeId(dto.getDepartment()));
        employee.setDepartment(dto.getDepartment());
        employee.setJobTitle(dto.getJobTitle());
        employee.setHireDate(LocalDate.now());
        employee.setSalary(dto.getSalary());
        employee.setEmploymentType(dto.getEmploymentType());
        employee.setWorkContactInfo(dto.getWorkContactInfo());
        employee.setEmergencyContact(dto.getEmergencyContact());
        employee.setActive(true);

        if (dto.getTerminationDate() != null) {
            employee.setTerminationDate(dto.getTerminationDate());
        }

        // Assign the specified role (required in DTO)
        RoleEntity role = roleRepository.findByName(dto.getRoleType())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + dto.getRoleType()));
        employee.addRole(role);

        return employeeRepository.save(employee);
    }

    public String getUserName(String email) {
        User user = userService.getUserByEmail(email);
        return user.getName().getFirstName() + " " + user.getName().getLastName();
    };

    // READ - Get all active employees
    public List<Employee> getAllActiveEmployees() {
        // return employeeRepository.findByIsActive(true);
        return employeeRepository.findAll();
    }

    // READ - Get employee by ID
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));
    }

    // READ - Get employees by department
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    // READ - Get all employees (active and inactive)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // UPDATE
    @Transactional
    public Employee updateEmployee(Long employeeId, EmployeeUpdateDto dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        PersonName name = new PersonName();
        name.setFirstName(dto.getName().getFirstName());
        name.setLastName(dto.getName().getLastName());

        // Update fields
        employee.setEmail(dto.getEmail());
        employee.setName(name);
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setTerminationDate(dto.getTerminationDate());

        employee.setDepartment(dto.getDepartment());
        employee.setJobTitle(dto.getJobTitle());
        employee.setSalary(dto.getSalary());
        employee.setEmploymentType(dto.getEmploymentType());
        employee.setWorkContactInfo(dto.getWorkContactInfo());
        employee.setEmergencyContact(dto.getEmergencyContact());

        // Update role if changed
        if (dto.getRoleType() != null) {
            validateEmployeeRole(dto.getRoleType());
            RoleEntity role = roleRepository.findByName(dto.getRoleType())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));
            employee.getRoles().clear();
            employee.addRole(role);
        }
        return employeeRepository.save(employee);
    }

    // DEACTIVATE (soft delete)
    @Transactional
    public void deactivateEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        employee.setActive(false);
        employee.setEnabled(false);
        employee.setStatus(UserStatus.DEACTIVATED);
        // employee.setTerminationDate(LocalDate.now());

        employeeRepository.save(employee);
    }

    // REACTIVATE
    @Transactional
    public void reactivateEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        employee.setActive(true);
        employee.setEnabled(true);
        employee.setStatus(UserStatus.ACTIVE);
        // employee.setTerminationDate(null);

        employeeRepository.save(employee);
    }

    // VALIDATION
    private void validateEmployeeRole(RoleType roleType) {
        if (roleType == RoleType.CUSTOMER) {
            throw new IllegalArgumentException("Employees cannot have CUSTOMER role");
        }

        // Only these 3 roles are valid for employees
        if (!Set.of(RoleType.AGENT, RoleType.CLAIM_MANAGER, RoleType.ADMIN).contains(roleType)) {
            throw new IllegalArgumentException("Invalid role for employee: " + roleType);
        }
    }

    // UTILITY METHODS
    private String generateEmployeeId(String department) {
        // Simple ID generation
        String prefix = department.substring(0, Math.min(3, department.length())).toUpperCase();
        long count = employeeRepository.count() + 1;
        return prefix + String.format("%04d", count);
    }

    // private RoleType determineRoleFromJobTitle(String jobTitle, RoleType
    // requestedRole) {
    // // If role is specified in DTO, use it
    // if (requestedRole != null) {
    // return requestedRole;
    // }

    // // Default mapping based on job title - only 4 roles now
    // return switch (jobTitle.toUpperCase()) {
    // case "AGENT", "SALES AGENT", "SALES REPRESENTATIVE", "BROKER" ->
    // RoleType.AGENT;
    // case "CLAIM MANAGER", "CLAIMS MANAGER", "CLAIMS ADJUSTER", "CLAIMS
    // SUPERVISOR" -> RoleType.CLAIM_MANAGER;
    // case "ADMIN", "SYSTEM ADMINISTRATOR", "ADMINISTRATOR", "SUPER ADMIN" ->
    // RoleType.ADMIN;

    // // Map legacy/old roles to appropriate new roles:
    // case "UNDERWRITER", "RISK ASSESSOR" -> RoleType.AGENT; // Underwriters become
    // AGENTS
    // case "AUDITOR", "COMPLIANCE OFFICER" -> RoleType.ADMIN; // Auditors become
    // ADMINS
    // case "CUSTOMER SUPPORT", "SUPPORT AGENT", "HELPDESK" -> RoleType.AGENT; //
    // Support becomes AGENT

    // // Default fallback - most employees should be AGENTS
    // default -> RoleType.AGENT;
    // };
    // }
}