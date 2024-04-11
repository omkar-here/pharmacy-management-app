package com.example.pharmacymanagement.appservice.repository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pharmacymanagement.appservice.entity.Inventory;
import com.example.pharmacymanagement.appservice.entity.Medicine;

public interface InventoryRepo extends JpaRepository<Inventory, Integer> {

    boolean existsByMedicineId(Medicine medicineId);

    List<Inventory> findByMedicineIdAndExpiryDateGreaterThanOrderByExpiryDateAsc(Medicine medicineId, LocalDate now);

    boolean existsByMedicineIdAndBatchNoAndSellerId(Medicine medicine, Integer batchNo, Integer sellerId);

    Inventory findByMedicineIdAndBatchNoAndSellerId(Medicine medicine, Integer batchNo, Integer sellerId);
    
}
