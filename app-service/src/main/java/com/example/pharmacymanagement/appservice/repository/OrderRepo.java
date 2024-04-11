package com.example.pharmacymanagement.appservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pharmacymanagement.appservice.entity.Order;

public interface OrderRepo extends JpaRepository<Order, Integer> {
    
}
