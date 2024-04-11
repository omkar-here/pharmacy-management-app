package com.example.pharmacymanagement.appservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pharmacymanagement.appservice.entity.Medicine;

public interface MedicineRepo extends JpaRepository<Medicine, Integer> {
    List<Medicine> findByNameContaining(String name);
    List<Medicine> findByNameContainingAndType(String name, String type); 
    Boolean existsByNameAndBrand(String name, String brand);
    List<Medicine> findByNameLikeIgnoreCaseAndType(String name, String type);
    List<Medicine> findByNameLikeIgnoreCase(String name);
} 

