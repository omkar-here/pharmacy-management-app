package com.example.pharmacymanagement.appservice.dto;

import com.example.pharmacymanagement.appservice.entity.OrderType;

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
