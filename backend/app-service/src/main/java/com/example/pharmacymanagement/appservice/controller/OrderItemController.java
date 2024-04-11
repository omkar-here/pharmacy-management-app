package com.example.pharmacymanagement.appservice.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.appservice.dto.OrderItemDto;
import com.example.pharmacymanagement.appservice.dto.Response;
import com.example.pharmacymanagement.appservice.entity.Client;
import com.example.pharmacymanagement.appservice.entity.Inventory;
import com.example.pharmacymanagement.appservice.entity.Medicine;
import com.example.pharmacymanagement.appservice.entity.Order;
import com.example.pharmacymanagement.appservice.entity.OrderItem;
import com.example.pharmacymanagement.appservice.entity.OrderType;
import com.example.pharmacymanagement.appservice.repository.ClientRepo;
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

    @Autowired
    ClientRepo clientRepo;

    /*
     * POST /order/{id}/add - response ok
     * GET /order/{id}/items - response ok data
     * DELETE /order/{id}/item/{itemId} - response ok
     * PATCH /order/{id}/item/{itemId} - response ok
     */

    @Transactional
    @PostMapping("/order/{id}/add")
    public ResponseEntity<Response> addItem(@PathVariable Integer id, @RequestBody OrderItemDto orderItem) {
        if (orderItem.getMedicineId() == null || orderItem.getQuantity() == null || orderItem.getQuantity() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order item data");
        Order order = orderRepo.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id: " + id + " not found"));
        Medicine medicine = medicineRepo.findById(orderItem.getMedicineId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Medicine with id: " + orderItem.getMedicineId() + " not found"));
        if (order.getType() == OrderType.OUTGOING) {
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
            if (orderItem.getPrice() == null || orderItem.getPrice() <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price is required");
            if (orderItem.getBatchNumber() == null || orderItem.getBatchNumber().equals(0))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Batch number is required");
            if (orderItem.getExpiryDate() == null || orderItem.getExpiryDate().isBefore(LocalDate.now()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid expiry date");
            Client client = clientRepo.findById(orderItem.getSellerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Client with id: " + orderItem.getSellerId() + " not found"));
            if (inventoryRepo.existsByMedicineIdAndBatchNoAndSellerId(medicine, orderItem.getBatchNumber(),
                    orderItem.getSellerId())) {
                Inventory inventory = inventoryRepo.findByMedicineIdAndBatchNoAndSellerId(medicine,
                        orderItem.getBatchNumber(), orderItem.getSellerId());
                inventory.setQuantity(inventory.getQuantity() + orderItem.getQuantity());
                inventoryRepo.save(inventory);
                OrderItem newOrderItem = OrderItem.builder()
                        .orderId(order)
                        .inventoryId(inventory)
                        .quantity(orderItem.getQuantity())
                        .price(inventory.getPrice())
                        .build();
                orderItemRepo.save(newOrderItem);
                return ResponseEntity.ok(Response.builder()
                        .build());
            } else {
                Inventory inventory = Inventory.builder()
                        .medicineId(medicine)
                        .batchNumber(orderItem.getBatchNumber())
                        .expiryDate(orderItem.getExpiryDate())
                        .price(orderItem.getPrice())
                        .quantity(orderItem.getQuantity())
                        .sellerId(client)
                        .build();
                inventoryRepo.save(inventory);
                OrderItem newOrderItem = OrderItem.builder()
                        .orderId(order)
                        .inventoryId(inventory)
                        .quantity(orderItem.getQuantity())
                        .price(orderItem.getPrice())
                        .build();
                orderItemRepo.save(newOrderItem);
                return ResponseEntity.ok(Response.builder()
                        .build());

            }
        }
    }

    @GetMapping("/order/{id}/items")
    public ResponseEntity<Response> getItems(@PathVariable Integer id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id: " + id + " not found"));
        List<OrderItem> orderItems = orderItemRepo.findByOrderId(order);
        return ResponseEntity.ok(Response.builder()
                .data(orderItems)
                .build());
    }

}
