package com.example.pharmacymanagement.appservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.appservice.dto.Response;
import com.example.pharmacymanagement.appservice.entity.Medicine;
import com.example.pharmacymanagement.appservice.repository.MedicineRepo;

@RestController
@RequestMapping("/medicine")
public class MedicineController {

    /*
        GET /medicine Response paginated
        GET /medicine/{id} Response medicine
        GET /medicine/search?name={name}&type={type} Response paginated
        POST /medicine Response ok
        PATCH /medicine/{id} Response ok
        DELETE /medicine/{id} Response ok
    */

    @Autowired
    MedicineRepo medicineRepo;

    @GetMapping("/")
    public ResponseEntity<Response> getMedicines(@RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (size != null && (size < 10 || size > 50))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size should be between 10 and 50");
        if (page != null && page < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page should be greater than or equal to 0");
        if (size == null)
            size = 10;
        if (page == null)
            page = 0;
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
    public ResponseEntity<Response> getMedicineById(Integer id) {
        return ResponseEntity.ok(Response.builder()
                .data(medicineRepo.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Medicine with id: " + id + " not found")))
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchMedicine(@RequestParam String name, @RequestParam String type) {
        if (name.isBlank())
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
            Page<Medicine> medicines = medicineRepo.findByNameLikeIgnoreCaseAndType(queryName, type,
                    PageRequest.of(0, 10));
            return ResponseEntity.ok(Response.builder()
                    .data(medicines.getContent())
                    .build());
        }
    }

    @PostMapping("/")
    public ResponseEntity<Response> addMedicine(@RequestBody Medicine medicine) {
        if (medicineRepo.existsByNameAndBrand(medicine.getName(), medicine.getBrand())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medicine with name: " + medicine.getName()
                    + " and brand: " + medicine.getBrand() + " already exists");
        }
        medicineRepo.save(medicine);
        return ResponseEntity.ok(Response.builder()
                .data("Medicine added successfully")
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response> updateMedicine(@RequestBody Medicine medicine, @RequestParam Integer id) {
        Medicine existingMedicine = medicineRepo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine with id: " + id + " not found"));
        if (medicine.getName() != null && !medicine.getName().isBlank())
            existingMedicine.setName(medicine.getName());
        if (medicine.getBrand() != null && !medicine.getBrand().isBlank())
            existingMedicine.setBrand(medicine.getBrand());
        if (medicine.getType() != null && !medicine.getType().isBlank())
            existingMedicine.setType(medicine.getType());
        return ResponseEntity.ok(Response.builder()
                .data("Medicine with id: " + id + " updated successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteMedicine(@RequestParam Integer id) {
        if (!medicineRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine with id: " + id + " not found");
        medicineRepo.deleteById(id);
        return ResponseEntity.ok(Response.builder()
                .data("Medicine with id: " + id + " deleted successfully")
                .build());
    }
}
