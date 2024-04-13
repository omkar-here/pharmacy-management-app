package com.example.pharmacymanagement.appservice.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.pharmacymanagement.appservice.entity.OrderItem;

public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findAllByOrderId(Integer orderId);
    Optional<OrderItem> findByIdAndOrderId(Integer itemId, Integer id);
}
