package com.example.pharmacymanagement.appservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pharmacymanagement.appservice.entity.Employee;

public interface EmployeeRepo extends JpaRepository<Employee, Integer> {
    Employee findByUsername(String username);
    
}
