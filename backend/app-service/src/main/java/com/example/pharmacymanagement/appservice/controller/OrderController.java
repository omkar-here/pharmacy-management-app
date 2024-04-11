package com.example.pharmacymanagement.appservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.appservice.dto.OrderDto;
import com.example.pharmacymanagement.appservice.dto.Response;
import com.example.pharmacymanagement.appservice.entity.Client;
import com.example.pharmacymanagement.appservice.entity.Employee;
import com.example.pharmacymanagement.appservice.entity.Order;
import com.example.pharmacymanagement.appservice.repository.ClientRepo;
import com.example.pharmacymanagement.appservice.repository.EmployeeRepo;
import com.example.pharmacymanagement.appservice.repository.OrderRepo;

@RestController
public class OrderController {
    /*
     * POST /order - response ok
     * GET /orders - response pagination data
     * GET /order/{id} - response ok data
     * DELETE /order/{id} - response ok
     */

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @PostMapping("/order")
    public ResponseEntity<Response> addOrder(@RequestBody OrderDto order) {
        Client client = clientRepo.findById(order.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Client with id: " + order.getCustomerId() + " not found"));
        Employee employee = employeeRepo.findById(order.getEmployeeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Employee with id: " + order.getEmployeeId() + " not found"));
        if (order.getType() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order type is required");
        Order newOrder = Order.builder()
                .customerId(client)
                .employeeId(employee)
                .type(order.getType())
                .build();
        orderRepo.save(newOrder);
        return ResponseEntity.ok(Response.builder()
                .data("Order added successfully")
                .build());
    }

    @GetMapping("/orders")
    public ResponseEntity<Response> getOrders(@RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (size != null && (size < 10 || size > 50))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size should be between 10 and 50");
        if (page != null && page < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page should be greater than or equal to 0");
        if (size == null)
            size = 10;
        if (page == null)
            page = 0;
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

    @GetMapping("/order/{id}")
    public ResponseEntity<Response> getOrderById(@PathVariable Integer id) {
        return ResponseEntity.ok(Response.builder()
                .data(orderRepo.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Order with id: " + id + " not found")))
                .build());
    }

    @DeleteMapping("/order/{id}")
    public ResponseEntity<Response> deleteOrder(@PathVariable Integer id) {
        if (orderRepo.existsById(id)) {
            orderRepo.deleteById(id);
            return ResponseEntity.ok(Response.builder()
                    .build());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id: " + id + " not found");
        }
    }
}
