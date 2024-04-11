package com.example.pharmacymanagement.appservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pharmacymanagement.appservice.entity.Client;

public interface ClientRepo extends JpaRepository<Client, Integer> {
    
}
