package com.example.pharmacymanagement.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pharmacymanagement.userservice.entity.Client;

public interface ClientRepo extends JpaRepository<Client, Integer> {
    Boolean existsByEmailAndPhone(String email, Long phone);

    Page<Client> findByNameLikeIgnoreCase(String queryName, PageRequest of);
}
