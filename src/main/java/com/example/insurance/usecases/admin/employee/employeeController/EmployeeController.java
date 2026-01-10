package com.example.insurance.usecases.admin.employee.employeeController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.example.insurance.domain.employee.domain.Employee;
import com.example.insurance.usecases.admin.employee.EmployeeCreateDto;
import com.example.insurance.usecases.admin.employee.EmployeeResponseDto;
import com.example.insurance.usecases.admin.employee.EmployeeService;
import com.example.insurance.usecases.admin.employee.EmployeeUpdateDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
// @PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/admin/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/create-employee")
    public ResponseEntity<EmployeeResponseDto> createEmployee(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody EmployeeCreateDto dto) {
        String userName = employeeService.getUserName(userDetails.getUsername());
        var employee = employeeService.createEmployee(dto, userName);
        return ResponseEntity.ok(mapToResponse(employee));
    }

    @GetMapping("/get-employees")
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        var employees = employeeService.getAllActiveEmployees();
        var response = employees.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee-details/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable Long id) {
        var employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(mapToResponse(employee));
    }

    @PatchMapping("/update-employee/{id}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateDto dto) {
        var employee = employeeService.updateEmployee(id, dto);
        return ResponseEntity.ok(mapToResponse(employee));
    }

    @PatchMapping("/deactivate-employee/{id}")
    public ResponseEntity<Void> deactivateEmployee(@PathVariable Long id) {
        employeeService.deactivateEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reactivate-employee/{id}")
    public ResponseEntity<Void> reactivateEmployee(@PathVariable Long id) {
        employeeService.reactivateEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<EmployeeResponseDto>> getEmployeesByDepartment(
            @PathVariable String department) {
        var employees = employeeService.getEmployeesByDepartment(department);
        var response = employees.stream()
                .map(this::mapToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    private EmployeeResponseDto mapToResponse(Employee employee) {
        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setId(employee.getId());
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setEmail(employee.getEmail());
        dto.setName(employee.getName());
        dto.setDateOfBirth(employee.getDateOfBirth());
        dto.setDepartment(employee.getDepartment());
        dto.setJobTitle(employee.getJobTitle());
        dto.setSalary(employee.getSalary());
        dto.setEmploymentType(employee.getEmploymentType());
        dto.setRoleType(employee.getRoles().stream()
                .findFirst()
                .map(role -> role.getName())
                .orElse(null));
        dto.setWorkContactInfo(employee.getWorkContactInfo());
        dto.setEmergencyContact(employee.getEmergencyContact());
        dto.setHireDate(employee.getHireDate());
        dto.setActive(employee.isActive());

        if (employee.getDateOfBirth() != null) {
            dto.setDateOfBirth(employee.getDateOfBirth());
        }
        if (employee.getTerminationDate() != null) {
            dto.setTerminationDate(employee.getTerminationDate());
        }

        return dto;
    }
}
