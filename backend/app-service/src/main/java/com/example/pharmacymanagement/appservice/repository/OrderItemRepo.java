package com.example.pharmacymanagement.appservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pharmacymanagement.appservice.entity.OrderItem;

public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {
    
}
