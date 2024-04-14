package com.example.pharmacymanagement.appservice.repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pharmacymanagement.appservice.entity.Order;
import com.example.pharmacymanagement.appservice.entity.OrderStatus;
import com.example.pharmacymanagement.appservice.entity.OrderType;

public interface OrderRepo extends JpaRepository<Order, Integer> {

    Page<Order> findByType(OrderType type, PageRequest of);

    Page<Order> findByStatus(OrderStatus status, PageRequest of);

    List<Order> findByStatusAndUpdatedAtBefore(OrderStatus ongoing, LocalDateTime twoHoursAgo);

    Optional<Order> findOneByCustomerIdAndStatus(Integer id, OrderStatus ongoing);
    
}
