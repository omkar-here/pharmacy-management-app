package com.example.pharmacymanagement.userservice.dto;

import com.example.pharmacymanagement.userservice.entity.EmployeeRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AuthToken {
    private Integer id;
    private EmployeeRole role;
}
