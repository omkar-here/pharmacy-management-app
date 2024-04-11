package com.example.pharmacymanagement.appservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.appservice.entity.Medicine;
import com.example.pharmacymanagement.appservice.repository.MedicineRepo;

@RestController
public class MedicineController {
    @Autowired
    MedicineRepo medicineRepo;

    @GetMapping("/medicines")
    public ResponseEntity<List<Medicine>> getMedicines(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if(size != null && (size<10 || size>50)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size should be between 10 and 50");
        if(size == null) size = 10;
        if(page != null) ResponseEntity.ok(medicineRepo.findAll().subList(page * size, (page + 1) * size));
        return ResponseEntity.ok(medicineRepo.findAll());
    }

    @GetMapping("/medicine/{id}")
    public ResponseEntity<Medicine> getMedicineById(Integer id) {
        return ResponseEntity.ok(medicineRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Medicine with id: " + id + " not found")));
    }

    @GetMapping("/medicine/search")
    public ResponseEntity<List<Medicine>> searchMedicine(@RequestParam String name, @RequestParam String type) {
        if(name.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be blank");
        if(name.length() < 3) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name should be at least 3 characters long");
        String queryName = "%" + name + "%";
        if(type.isBlank() || type.equalsIgnoreCase("all")) {
            return ResponseEntity.ok(medicineRepo.findByNameLikeIgnoreCase(queryName).stream().limit(10).toList());
        } else {
            return ResponseEntity.ok(medicineRepo.findByNameLikeIgnoreCaseAndType(queryName, type).stream().limit(10).toList());
        }
    }

    @PostMapping("/medicine")
    public ResponseEntity<Medicine> addMedicine(@RequestBody Medicine medicine) {
        if(medicineRepo.existsByNameAndBrand(medicine.getName(), medicine.getBrand())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medicine with name: " + medicine.getName() + " and brand: " + medicine.getBrand() + " already exists");
        }
        medicineRepo.save(medicine);
        return ResponseEntity.ok(medicine);
    }

    @PatchMapping("/medicine/{id}")
    public ResponseEntity<Medicine> updateMedicine(@RequestBody Medicine medicine, @RequestParam Integer id) {
        Medicine existingMedicine = medicineRepo.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine with id: " + id + " not found")
        );
        if(!medicine.getName().isBlank()) existingMedicine.setName(medicine.getName());
        if(!medicine.getBrand().isBlank()) existingMedicine.setBrand(medicine.getBrand());
        if(!medicine.getType().isBlank()) existingMedicine.setType(medicine.getType());
        return ResponseEntity.ok(medicineRepo.save(existingMedicine));
    }

    @DeleteMapping("/medicine/{id}")
    public ResponseEntity<String> deleteMedicine(@RequestParam Integer id) {
        if(!medicineRepo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine with id: " + id + " not found");
        medicineRepo.deleteById(id);
        return ResponseEntity.ok("Medicine with id: " + id + " deleted successfully");
    }
}
