package com.example.pharmacymanagement.authservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pharmacymanagement.authservice.entity.Employee;

public interface EmployeeRepo extends JpaRepository<Employee, Integer> {
    Employee findByUsername(String username);
    
}
