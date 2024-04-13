package com.example.pharmacymanagement.appservice.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pharmacymanagement.appservice.entity.Employee;

public interface EmployeeRepo extends JpaRepository<Employee, Integer> {
    Employee findByUsername(String username);

    Optional<Employee> findOneByUsername(String username);

    boolean existsByUsername(String username);
    
}
