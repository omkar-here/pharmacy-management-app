package com.example.pharmacymanagement.appservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pharmacymanagement.appservice.entity.Medicine;

public interface MedicineRepo extends JpaRepository<Medicine, Integer> {
    List<Medicine> findByNameContaining(String name);
    List<Medicine> findByNameContainingAndType(String name, String type); 
    Boolean existsByNameAndBrand(String name, String brand);
    List<Medicine> findByNameLikeIgnoreCaseAndType(String name, String type);
    List<Medicine> findByNameLikeIgnoreCase(String name);
    Page<Medicine> findByNameLikeIgnoreCase(String queryName, PageRequest of);
    Page<Medicine> findByNameLikeIgnoreCaseAndType(String queryName, String type, PageRequest of);
} 

