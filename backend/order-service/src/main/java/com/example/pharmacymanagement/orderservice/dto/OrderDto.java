package com.example.pharmacymanagement.orderservice.dto;

import com.example.pharmacymanagement.orderservice.entity.OrderType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private Integer customerId;
    private Integer employeeId;
    private OrderType type;
}
