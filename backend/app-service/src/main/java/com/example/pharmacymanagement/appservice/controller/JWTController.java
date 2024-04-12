package com.example.pharmacymanagement.appservice.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.appservice.dto.Response;
import com.example.pharmacymanagement.appservice.entity.Employee;
import com.example.pharmacymanagement.appservice.repository.EmployeeRepo;
import com.example.pharmacymanagement.appservice.service.JwtService;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class JWTController {
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmployeeRepo employeeRepo;

    @GetMapping("/authtoken")
    public ResponseEntity<Response> getAuthToken(
        @CookieValue("refreshToken") String token) {
        if(token.isBlank() || !jwtService.validateRefreshToken(token)) 
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        Integer id = (Integer)jwtService.getClaimsFromToken(token, true).get("id");
        Employee emp = employeeRepo.findById(id)
            .orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Employee not found"));
        return ResponseEntity.ok(
        Response.builder().data(jwtService.createAuthToken(emp.getUsername(), emp.getId(), emp.getRole())).build());
    }
    
}
