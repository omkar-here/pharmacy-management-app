package com.example.pharmacymanagement.orderservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.pharmacymanagement.orderservice.entity.OrderItem;

public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findAllByOrderId(Integer orderId);

    Optional<OrderItem> findByIdAndOrderId(Integer itemId, Integer id);
}
