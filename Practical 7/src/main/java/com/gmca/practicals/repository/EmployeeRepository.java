package com.gmca.practicals.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gmca.practicals.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
