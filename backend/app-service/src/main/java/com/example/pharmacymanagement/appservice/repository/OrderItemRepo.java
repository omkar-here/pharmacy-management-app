package com.example.pharmacymanagement.appservice.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pharmacymanagement.appservice.entity.Order;
import com.example.pharmacymanagement.appservice.entity.OrderItem;

public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findByOrderId(Order order);
    
}
