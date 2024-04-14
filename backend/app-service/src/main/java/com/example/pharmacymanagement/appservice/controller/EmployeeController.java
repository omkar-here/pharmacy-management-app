package com.example.pharmacymanagement.appservice.controller;

import java.util.Objects;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.appservice.repository.EmployeeRepo;
import com.example.pharmacymanagement.appservice.dto.Response;
import com.example.pharmacymanagement.appservice.entity.Employee;

@RestController
@CrossOrigin
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    EmployeeRepo employeeRepo;

    /*
     * GET /employees - Response ok paginated
     * GET /employee/{id} - Response ok
     * POST /employee - Response ok
     * PATCH /employee/{id} - Response ok
     * DELETE /employee/{id} - Response ok
     */

    @CrossOrigin
    @GetMapping("/all")
    public ResponseEntity<Response> getEmployees(@RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(defaultValue = "10", required = false) Integer size) {
        if (size < 10 || size > 50)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size should be between 10 and 50");
        if (page < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page should be greater than or equal to 0");
        Page<Employee> employees = employeeRepo.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(Response.builder()
                .data(employees.getContent())
                .page(page)
                .size(size)
                .totalPages(employees.getTotalPages())
                .hasNext(employees.hasNext())
                .hasPrevious(employees.hasPrevious())
                .build());
    }

    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<Response> getEmployeeById(@PathVariable Integer id) {
        return ResponseEntity.ok(Response.builder()
                .data(employeeRepo.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Employee with id: " + id + " not found")))
                .build());
    }

    @CrossOrigin
    @PostMapping("/add")
    public ResponseEntity<Response> addEmployee(@RequestBody Employee employee) {
        if (Objects.isNull(employee.getName()) || Objects.isNull(employee.getPassword()) ||
                Objects.isNull(employee.getUsername()) || Objects.isNull(employee.getRole()) ||
                employee.getName().isBlank() || employee.getPassword().isBlank() || employee.getUsername().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid employee data");
        if (employeeRepo.existsByUsername(employee.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee already exists");
        employee.setPassword(BCrypt.hashpw(employee.getPassword(), BCrypt.gensalt()));
        employeeRepo.save(employee);
        return ResponseEntity.ok(Response.builder()
                .data("Employee added successfully")
                .build());
    }

    @CrossOrigin
    @PatchMapping("/{id}")
    public ResponseEntity<Response> updateEmployee(@RequestBody Employee employee, @PathVariable Integer id) {
        Employee existingEmployee = employeeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Employee with id: " + id + " not found"));
        if (!Objects.isNull(existingEmployee.getName()) && !existingEmployee.getName().isBlank())
            existingEmployee.setName(employee.getName());
        employeeRepo.save(existingEmployee);
        return ResponseEntity.ok(Response.builder()
                .data("Employee with id: " + id + " updated")
                .build());
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteEmployee(@PathVariable Integer id) {
        if (employeeRepo.existsById(id)) {
            employeeRepo.deleteById(id);
            return ResponseEntity.ok(Response.builder()
                    .data("Employee with id: " + id + " deleted")
                    .build());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee with id: " + id + " not found");
        }
    }
}
