package com.example.pharmacymanagement.authservice.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.authservice.entity.Employee;
import com.example.pharmacymanagement.authservice.repository.EmployeeRepo;
import com.example.pharmacymanagement.authservice.service.JwtService;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class JWTController {
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmployeeRepo employeeRepo;

    @GetMapping("/authtoken")
    public String getAuthToken(@CookieValue("refreshToken") String token) {
        if(token.isBlank() || !jwtService.validateRefreshToken(token)) 
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        Integer id = (Integer)jwtService.getClaimsFromToken(token, true).get("id");
        Optional<Employee> emp = employeeRepo.findById(id);
        if(emp.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        return jwtService.createAuthToken(emp.get().getUsername(), emp.get().getId(), emp.get().getRole());
    }
    
}
