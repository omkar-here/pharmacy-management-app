package com.example.pharmacymanagement.orderservice.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private Integer quantity;
    private Integer medicineId;
    private LocalDate expiryDate;
    private Integer sellerId;
    private Integer batchNumber;
    private Double price;
}
