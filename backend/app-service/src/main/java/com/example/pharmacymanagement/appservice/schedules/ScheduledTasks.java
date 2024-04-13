package com.example.pharmacymanagement.appservice.schedules;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.pharmacymanagement.appservice.entity.OrderStatus;
import com.example.pharmacymanagement.appservice.repository.OrderRepo;

import jakarta.transaction.Transactional;

@EnableScheduling
@Component
public class ScheduledTasks {

    @Autowired
    OrderRepo orderRepo;

    @Transactional
    @Scheduled(fixedRate = 1000 * 60 * 60 * 2)
    public void cancelOldOrders() {
        LocalDateTime twoHoursAgo = LocalDateTime.now().minus(2, ChronoUnit.HOURS);
        orderRepo.findByStatusAndUpdatedAtBefore(OrderStatus.ONGOING, twoHoursAgo)
                .forEach(order -> {
                    order.setStatus(OrderStatus.CANCELLED);
                    orderRepo.save(order);
                });
    }
}
