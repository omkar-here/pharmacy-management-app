package com.example.pharmacymanagement.appservice.controller;

import java.util.Objects;
import java.util.Optional;

import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.appservice.dto.Response;
import com.example.pharmacymanagement.appservice.entity.Employee;
import com.example.pharmacymanagement.appservice.repository.EmployeeRepo;
import com.example.pharmacymanagement.appservice.service.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    /*
     * POST /login Response authToken
     * GET /logout Response ok
     * GET /authToken Response authToken
     */

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmployeeRepo employeeRepo;

    @GetMapping("/authToken")
    public ResponseEntity<Response> getAuthToken(
            @CookieValue(name = "refreshToken", required = true) String token) {
        if (token.isBlank() || !jwtService.validateRefreshToken(token))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        Integer id = (Integer) jwtService.getClaimsFromToken(token, true).get("id");
        Employee emp = employeeRepo.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Employee not found"));
        return ResponseEntity.ok(
                Response.builder().data(
                        Map.of("authToken",
                                jwtService.createAuthToken(emp.getUsername(), emp.getId(), emp.getRole())))
                        .build());
    }

    @GetMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(Response.builder().build());
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(
            @CookieValue(name = "refreshToken", required = false) String token,
            @RequestBody Employee employeeBody,
            HttpServletResponse response) {
        if (!Objects.isNull(token) && !token.isBlank() && jwtService.validateRefreshToken(token)) {
            Integer id = (Integer) jwtService.getClaimsFromToken(token, true).get("id");
            Optional<Employee> empOpt = employeeRepo.findById(id);
            if (empOpt.isPresent()) {
                Employee employee = empOpt.get();
                return ResponseEntity.ok(Response.builder().data(
                        jwtService.createAuthToken(employee.getUsername(), employee.getId(), employee.getRole()))
                        .build());
            }
        }
        if (Objects.isNull(employeeBody) || Objects.isNull(employeeBody.getUsername())
                || Objects.isNull(employeeBody.getPassword()) || employeeBody.getUsername().isBlank()
                || employeeBody.getPassword().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required fields are missing");
        Employee employee = employeeRepo.findOneByUsername(employeeBody.getUsername())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee not found"));
        if (BCrypt.checkpw(employeeBody.getPassword(), employee.getPassword())) {
            Cookie cookie = new Cookie("refreshToken", jwtService.createRefreshToken(employee.getId()));
            cookie.setHttpOnly(true);
            cookie.setMaxAge(JwtService.REFRESH_VALIDITY);
            response.addCookie(cookie);
            return ResponseEntity.ok(Response.builder()
                    .data(Map.of("authToken",
                            jwtService.createAuthToken(employee.getUsername(), employee.getId(), employee.getRole())))
                    .build());

        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

    }
}
