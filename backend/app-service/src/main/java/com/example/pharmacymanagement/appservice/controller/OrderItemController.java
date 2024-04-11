package com.example.pharmacymanagement.appservice.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.appservice.dto.OrderItemDto;
import com.example.pharmacymanagement.appservice.dto.Response;
import com.example.pharmacymanagement.appservice.entity.Inventory;
import com.example.pharmacymanagement.appservice.entity.Medicine;
import com.example.pharmacymanagement.appservice.entity.Order;
import com.example.pharmacymanagement.appservice.entity.OrderItem;
import com.example.pharmacymanagement.appservice.entity.OrderType;
import com.example.pharmacymanagement.appservice.repository.InventoryRepo;
import com.example.pharmacymanagement.appservice.repository.MedicineRepo;
import com.example.pharmacymanagement.appservice.repository.OrderItemRepo;
import com.example.pharmacymanagement.appservice.repository.OrderRepo;

@RestController
public class OrderItemController {
    @Autowired
    OrderItemRepo orderItemRepo;

    @Autowired
    InventoryRepo inventoryRepo;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    MedicineRepo medicineRepo;

    /*
        POST /order/{id}/add - response ok
        GET /order/{id}/items - response ok data
        DELETE /order/{id}/item/{itemId} - response ok
        PATCH /order/{id}/item/{itemId} - response ok
    */

    @Transactional
    @PostMapping("/order/{id}/add")
    public ResponseEntity<Response> addItem(@PathVariable Integer id, @RequestBody OrderItemDto orderItem) {
        Order order = orderRepo.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id: " + id + " not found"));
        if (order.getType() == OrderType.OUTGOING) {
            Medicine medicine = medicineRepo.findById(orderItem.getMedicineId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Medicine with id: " + orderItem.getMedicineId() + " not found"));
            List<Inventory> inventories = inventoryRepo.findByMedicineIdAndExpiryDateGreaterThanOrderByExpiryDateAsc(
                medicine, LocalDate.now());
            Integer InvQuantity = inventories.stream().map(Inventory::getQuantity).reduce(0, Integer::sum);
            if (InvQuantity < orderItem.getQuantity())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough quantity available.");
            for (Inventory inv : inventories) {
                if (orderItem.getQuantity() > 0) {
                    Integer quantity = Math.min(orderItem.getQuantity(), inv.getQuantity());
                    OrderItem newOrderItem = OrderItem.builder()
                            .orderId(order)
                            .inventoryId(inv)
                            .quantity(quantity)
                            .price(inv.getPrice())
                            .build();
                    orderItemRepo.save(newOrderItem);
                    inv.setQuantity(inv.getQuantity() - quantity);
                    orderItem.setQuantity(orderItem.getQuantity() - quantity);
                } else
                    break;
            }
            return ResponseEntity.ok(Response.builder()
                    .build());
        } else {
            return ResponseEntity.ok(Response.builder()
                    .build());
        }
    }

}
