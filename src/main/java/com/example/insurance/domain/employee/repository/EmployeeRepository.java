package com.example.insurance.domain.employee.repository;

import com.example.insurance.domain.employee.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeId(String employeeId);

    Optional<Employee> findByEmail(String email);

    List<Employee> findByDepartment(String department);

    List<Employee> findByJobTitle(String jobTitle);

    List<Employee> findByIsActive(boolean isActive);

    @Query("SELECT e FROM Employee e WHERE e.department = :department AND e.isActive = true")
    List<Employee> findActiveEmployeesByDepartment(@Param("department") String department);

    // Add this if you want to filter by role
    @Query("SELECT e FROM Employee e JOIN e.roles r WHERE r.name = :roleName AND e.isActive = true")
    List<Employee> findByRoleName(@Param("roleName") String roleName);

    // Count active employees
    long countByIsActive(boolean isActive);
}