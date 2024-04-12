package com.example.pharmacymanagement.appservice.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.pharmacymanagement.appservice.dto.OrderItemDto;
import com.example.pharmacymanagement.appservice.entity.Order;
import com.example.pharmacymanagement.appservice.entity.OrderItem;

public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findAllByOrderId(Integer orderId);
    Optional<OrderItem> findByIdAndOrderId(Integer itemId, Integer id);
}
