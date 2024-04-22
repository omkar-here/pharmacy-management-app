package com.example.pharmacymanagement.orderservice.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.orderservice.dto.OrderItemDto;
import com.example.pharmacymanagement.orderservice.dto.Response;
import com.example.pharmacymanagement.orderservice.entity.Inventory;
import com.example.pharmacymanagement.orderservice.entity.Medicine;
import com.example.pharmacymanagement.orderservice.entity.Order;
import com.example.pharmacymanagement.orderservice.entity.OrderItem;
import com.example.pharmacymanagement.orderservice.entity.OrderStatus;
import com.example.pharmacymanagement.orderservice.entity.OrderType;
import com.example.pharmacymanagement.orderservice.repository.InventoryRepo;
import com.example.pharmacymanagement.orderservice.repository.MedicineRepo;
import com.example.pharmacymanagement.orderservice.repository.OrderItemRepo;
import com.example.pharmacymanagement.orderservice.repository.OrderRepo;
import com.example.pharmacymanagement.orderservice.service.UserService;

@RestController

@RequestMapping("/order")
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
        UserService userService;

        /*
         * POST /order/{id}/add - response ok
         * GET /order/{id}/items - response ok data
         * DELETE /order/{id}/item/{itemId} - response ok
         */

        @Transactional
        @PostMapping("/{id}/add")
        public ResponseEntity<Response> addItem(@PathVariable Integer orderId,
                        @RequestBody OrderItemDto orderItemBody) {
                if (Objects.isNull(orderItemBody.getMedicineId()) || Objects.isNull(orderItemBody.getQuantity())
                                || orderItemBody.getQuantity() <= 0)
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order item data");
                Order order = orderRepo.findById(orderId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Order with id: " + orderId + " not found"));
                Medicine medicine = medicineRepo.findById(orderItemBody.getMedicineId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Medicine with id: " + orderItemBody.getMedicineId() + " not found"));
                if (order.getType() == OrderType.OUTGOING) {
                        List<Inventory> inventories = inventoryRepo
                                        .findByMedicineIdAndStagedAndExpiryDateGreaterThanOrderByExpiryDateAsc(
                                                        medicine.getId(), false, LocalDate.now());
                        Long invQuantity = inventories.stream().map(Inventory::getQuantity).map(Integer::longValue)
                                        .reduce(0L, Long::sum);
                        if (invQuantity < orderItemBody.getQuantity())
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Not enough quantity available.");
                        for (Inventory inv : inventories) {
                                if (orderItemBody.getQuantity() > 0) {
                                        Integer quantity = Math.min(orderItemBody.getQuantity(), inv.getQuantity());
                                        OrderItem newOrderItem = OrderItem.builder()
                                                        .orderId(order.getId())
                                                        .inventoryId(inv.getId())
                                                        .quantity(quantity)
                                                        .price(inv.getPrice())
                                                        .build();
                                        orderItemRepo.save(newOrderItem);
                                        inv.setQuantity(inv.getQuantity() - quantity);
                                        inventoryRepo.save(inv);
                                        orderItemBody.setQuantity(orderItemBody.getQuantity() - quantity);
                                } else
                                        break;
                        }
                } else {
                        if (Objects.isNull(orderItemBody.getSellerId()))
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller id is required");
                        if (Objects.isNull(orderItemBody.getPrice()) || orderItemBody.getPrice() <= 0)
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price is required");
                        if (Objects.isNull(orderItemBody.getBatchNumber()) || orderItemBody.getBatchNumber().equals(0))
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Batch number is required");
                        if (Objects.isNull(orderItemBody.getExpiryDate())
                                        || orderItemBody.getExpiryDate().isBefore(LocalDate.now()))
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid expiry date");
                        if (!userService.findClientById(orderItemBody.getSellerId()).blockOptional().orElse(false))
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Client with id: " + orderItemBody.getSellerId() + " not found");
                        Inventory inventory = Inventory.builder()
                                        .medicineId(medicine.getId())
                                        .batchNumber(orderItemBody.getBatchNumber())
                                        .expiryDate(orderItemBody.getExpiryDate())
                                        .price(orderItemBody.getPrice())
                                        .quantity(orderItemBody.getQuantity())
                                        .sellerId(orderItemBody.getSellerId())
                                        .staged(true)
                                        .build();
                        inventory = inventoryRepo.save(inventory);
                        OrderItem newOrderItem = OrderItem.builder()
                                        .orderId(order.getId())
                                        .inventoryId(inventory.getId())
                                        .quantity(orderItemBody.getQuantity())
                                        .price(orderItemBody.getPrice())
                                        .build();
                        orderItemRepo.save(newOrderItem);
                }
                return ResponseEntity.ok(Response.builder()
                                .build());
        }

        @Transactional
        @GetMapping("/{id}/items")
        public ResponseEntity<Response> getItems(@PathVariable Integer orderId) {
                Order order = orderRepo.findById(orderId)
                                .orElseThrow(
                                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                                "Order with id: " + orderId + " not found"));
                List<OrderItem> orderItems = orderItemRepo.findAllByOrderId(order.getId());
                List<OrderItemDto> orderItemsDto = orderItems.stream().map((orderItem) -> {
                        Inventory inv = inventoryRepo.findById(orderItem.getInventoryId())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                        "Inventory with id: " + orderItem.getInventoryId()
                                                                        + " not found"));
                        return OrderItemDto.builder()
                                        .quantity(orderItem.getQuantity())
                                        .medicineId(inv.getMedicineId())
                                        .price(inv.getPrice())
                                        .expiryDate(inv.getExpiryDate())
                                        .sellerId(inv.getSellerId())
                                        .batchNumber(inv.getBatchNumber())
                                        .build();
                }).collect(Collectors.toList());
                return ResponseEntity.ok(Response.builder()
                                .data(orderItemsDto)
                                .build());
        }

        @Transactional
        @DeleteMapping("/order/{id}/item/{itemId}")
        public ResponseEntity<Response> deleteItem(@PathVariable Integer orderId, @PathVariable Integer orderItemId) {
                Order order = orderRepo.findById(orderId)
                                .orElseThrow(
                                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                                "Order with id: " + orderId + " not found"));
                if (!order.getStatus().equals(OrderStatus.ONGOING))
                        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                                        "Order is already " + order.getStatus());
                OrderItem orderItem = orderItemRepo.findByIdAndOrderId(orderItemId, order.getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Order item with id: " + orderItemId + " not found"));
                if (order.getType().equals(OrderType.OUTGOING)) {
                        Inventory inv = inventoryRepo.findById(orderItem.getInventoryId())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                        "Inventory with id: " + orderItem.getInventoryId()
                                                                        + " not found"));
                        inv.setQuantity(inv.getQuantity() + orderItem.getQuantity());
                        inventoryRepo.save(inv);
                        orderRepo.deleteById(orderItem.getId());
                }
                return ResponseEntity.ok(Response.builder()
                                .build());
        }
}
