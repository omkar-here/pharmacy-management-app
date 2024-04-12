package com.example.pharmacymanagement.appservice.repository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pharmacymanagement.appservice.entity.Inventory;
import com.example.pharmacymanagement.appservice.entity.Medicine;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Integer> {

    boolean existsByMedicineId(Integer medicineId);

    List<Inventory> findByMedicineIdAndExpiryDateGreaterThanOrderByExpiryDateAsc(Integer medicineId, LocalDate now);

    boolean existsByMedicineIdAndBatchNumberAndSellerId(Integer medicine, Integer batchNumber, Integer sellerId);

    Inventory findByMedicineIdAndBatchNumberAndSellerId(Integer medicine, Integer batchNumber, Integer sellerId);
    
}
