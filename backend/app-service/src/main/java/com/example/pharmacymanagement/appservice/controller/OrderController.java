package com.example.pharmacymanagement.appservice.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.appservice.dto.OrderDto;
import com.example.pharmacymanagement.appservice.dto.Response;
import com.example.pharmacymanagement.appservice.entity.Client;
import com.example.pharmacymanagement.appservice.entity.Employee;
import com.example.pharmacymanagement.appservice.entity.Inventory;
import com.example.pharmacymanagement.appservice.entity.Order;
import com.example.pharmacymanagement.appservice.entity.OrderItem;
import com.example.pharmacymanagement.appservice.entity.OrderStatus;
import com.example.pharmacymanagement.appservice.entity.OrderType;
import com.example.pharmacymanagement.appservice.repository.ClientRepo;
import com.example.pharmacymanagement.appservice.repository.EmployeeRepo;
import com.example.pharmacymanagement.appservice.repository.InventoryRepo;
import com.example.pharmacymanagement.appservice.repository.OrderItemRepo;
import com.example.pharmacymanagement.appservice.repository.OrderRepo;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/order")
public class OrderController {
    /*
     * POST /order - response ok
     * GET /orders - response pagination data
     * GET /order/{id} - response ok data
     * GET /order/type/{type} - response pagination data
     * GET /order/status/{status} - response pagination data
     * DELETE /order/{id} - response ok
     */

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private InventoryRepo inventoryRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<Response> addOrder(@RequestBody OrderDto orderBody) {
        if (Objects.isNull(orderBody.getCustomerId()) || Objects.isNull(orderBody.getEmployeeId())
                || Objects.isNull(orderBody.getType()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required fields missing");
        Client client = clientRepo.findById(orderBody.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Client with id: " + orderBody.getCustomerId() + " not found"));
        Employee employee = employeeRepo.findById(orderBody.getEmployeeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Employee with id: " + orderBody.getEmployeeId() + " not found"));
        Optional<Order> existingOrder = orderRepo.findOneByCustomerIdAndStatus(client.getId(), OrderStatus.ONGOING);
        if (existingOrder.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client already has an ongoing order");
        Order newOrder = Order.builder()
                .customerId(client.getId())
                .employeeId(employee.getId())
                .type(orderBody.getType())
                .build();
        orderRepo.save(newOrder);
        return ResponseEntity.ok(Response.builder()
                .build());
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getOrders(@RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        if (size < 10 || size > 50)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size should be between 10 and 50");
        if (page < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page should be greater than or equal to 0");
        Page<Order> orders = orderRepo.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(Response.builder()
                .data(orders.getContent())
                .page(page)
                .size(size)
                .totalPages(orders.getTotalPages())
                .hasNext(orders.hasNext())
                .hasPrevious(orders.hasPrevious())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getOrderById(@PathVariable Integer id) {
        return ResponseEntity.ok(Response.builder()
                .data(orderRepo.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Order with id: " + id + " not found")))
                .build());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Response> getOrdersByType(@PathVariable OrderType type,
            @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        if (size < 10 || size > 50)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size should be between 10 and 50");
        if (page < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page should be greater than or equal to 0");
        Page<Order> orders = orderRepo.findByType(type, PageRequest.of(page, size));
        return ResponseEntity.ok(Response.builder()
                .data(orders.getContent())
                .page(page)
                .size(size)
                .totalPages(orders.getTotalPages())
                .hasNext(orders.hasNext())
                .hasPrevious(orders.hasPrevious())
                .build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Response> getOrdersByStatus(@PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        if (size < 10 || size > 50)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size should be between 10 and 50");
        if (page < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page should be greater than or equal to 0");
        Page<Order> orders = orderRepo.findByStatus(status, PageRequest.of(page, size));
        return ResponseEntity.ok(Response.builder()
                .data(orders.getContent())
                .page(page)
                .size(size)
                .totalPages(orders.getTotalPages())
                .hasNext(orders.hasNext())
                .hasPrevious(orders.hasPrevious())
                .build());
    }

    @Transactional
    @PostMapping("/cancel/{id}")
    public ResponseEntity<Response> deleteOrder(@PathVariable Integer id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id: " + id + " not found"));
        if (!order.getStatus().equals(OrderStatus.ONGOING))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order with id: " + id + " is already completed");
        order.setStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);
        return ResponseEntity.ok(Response.builder()
                .build());
    }

    @Transactional
    @PostMapping("/checkout/{id}")
    public ResponseEntity<Response> checkoutOrder(@PathVariable Integer id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id: " + id + " not found"));
        if (!order.getStatus().equals(OrderStatus.ONGOING))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order with id: " + id + " is already completed");
        List<OrderItem> orderItems = orderItemRepo.findAllByOrderId(order.getId());
        if (orderItems.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order with id: " + id + " has no items");
        if (order.getType().equals(OrderType.OUTGOING)) {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepo.save(order);
            return ResponseEntity.ok(Response.builder()
                    .build());
        } else {
            orderItems.stream().forEach(orderItem -> {
                Inventory inventory = inventoryRepo.findById(orderItem.getInventoryId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Inventory with id: " + orderItem.getInventoryId() + " not found"));
                inventory.setStaged(false);
                inventoryRepo.save(inventory);
            });
            order.setStatus(OrderStatus.COMPLETED);
            orderRepo.save(order);
            return ResponseEntity.ok(Response.builder()
                    .build());
        }
    }
}
