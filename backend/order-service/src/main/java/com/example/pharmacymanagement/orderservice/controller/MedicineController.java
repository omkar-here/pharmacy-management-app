package com.example.pharmacymanagement.orderservice.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.orderservice.dto.EmployeeRole;
import com.example.pharmacymanagement.orderservice.dto.Response;
import com.example.pharmacymanagement.orderservice.entity.Medicine;
import com.example.pharmacymanagement.orderservice.repository.MedicineRepo;

@RestController
@RequestMapping("/medicine")
public class MedicineController {

    /*
     * GET /medicine Response paginated
     * GET /medicine/{id} Response medicine
     * GET /medicine/search?name={name}&type={type} Response paginated
     * POST /medicine Response ok
     * PATCH /medicine/{id} Response ok
     * DELETE /medicine/{id} Response ok
     */

    @Autowired
    MedicineRepo medicineRepo;

    @GetMapping("/all")
    public ResponseEntity<Response> getMedicines(@RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(defaultValue = "10", required = false) Integer size) {
        if (size < 10 || size > 50)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size should be between 10 and 50");
        if (page < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page should be greater than or equal to 0");
        Page<Medicine> medicines = medicineRepo.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(Response.builder()
                .data(medicines.getContent())
                .page(page)
                .size(size)
                .totalPages(medicines.getTotalPages())
                .hasNext(medicines.hasNext())
                .hasPrevious(medicines.hasPrevious())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getMedicineById(@PathVariable Integer id) {
        return ResponseEntity.ok(Response.builder()
                .data(medicineRepo.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Medicine with id: " + id + " not found")))
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchMedicine(@RequestParam String name,
            @RequestParam(defaultValue = "all") String type) {
        if (Objects.isNull(name) || name.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be blank");
        if (name.length() < 3)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name should be at least 3 characters long");
        String queryName = "%" + name + "%";
        if (type.isBlank() || type.equalsIgnoreCase("all")) {
            Page<Medicine> medicines = medicineRepo.findByNameLikeIgnoreCase(queryName, PageRequest.of(0, 10));
            return ResponseEntity.ok(Response.builder()
                    .data(medicines.getContent())
                    .build());
        } else {
            Page<Medicine> medicines = medicineRepo.findByNameLikeIgnoreCaseAndTypeIgnoreCase(queryName, type,
                    PageRequest.of(0, 10));
            return ResponseEntity.ok(Response.builder()
                    .data(medicines.getContent())
                    .build());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Response> addMedicine(
            @RequestHeader("x-employee-role") EmployeeRole role,
            @RequestBody Medicine medicineBody) {
        if (role != EmployeeRole.MANAGER)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only managers can add medicine");
        if (Objects.isNull(medicineBody.getName()) || Objects.isNull(medicineBody.getBrand())
                || Objects.isNull(medicineBody.getType()) ||
                medicineBody.getName().isBlank() || medicineBody.getBrand().isBlank()
                || medicineBody.getType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name, brand and type cannot be blank");
        }
        if (medicineRepo.existsByNameIgnoreCaseAndBrandIgnoreCase(medicineBody.getName(), medicineBody.getBrand())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medicine with name: " + medicineBody.getName()
                    + " and brand: " + medicineBody.getBrand() + " already exists");
        }
        medicineRepo.save(medicineBody);
        return ResponseEntity.ok(Response.builder()
                .data("Medicine added successfully")
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response> updateMedicine(
            @RequestHeader("x-employee-role") EmployeeRole role,
            @RequestBody Medicine medicineBody, @PathVariable Integer id) {
        if (role != EmployeeRole.MANAGER)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only managers can update medicine");
        Medicine existingMedicine = medicineRepo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine with id: " + id + " not found"));
        if (!Objects.isNull(medicineBody.getName()) && !medicineBody.getName().isBlank()) {
            existingMedicine.setName(medicineBody.getName());
        }
        if (!Objects.isNull(medicineBody.getBrand()) && !medicineBody.getBrand().isBlank()) {
            existingMedicine.setBrand(medicineBody.getBrand());
        }
        if (!Objects.isNull(medicineBody.getType()) && !medicineBody.getType().isBlank()) {
            existingMedicine.setType(medicineBody.getType());
        }
        medicineRepo.save(existingMedicine);
        return ResponseEntity.ok(Response.builder()
                .data("Medicine with id: " + id + " updated successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteMedicine(@PathVariable Integer id) {
        if (!medicineRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine with id: " + id + " not found");
        medicineRepo.deleteById(id);
        return ResponseEntity.ok(Response.builder()
                .data("Medicine with id: " + id + " deleted successfully")
                .build());
    }
}
