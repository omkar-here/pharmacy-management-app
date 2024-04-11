package com.example.pharmacymanagement.appservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.pharmacymanagement.appservice.dto.Response;
import com.example.pharmacymanagement.appservice.entity.Order;
import com.example.pharmacymanagement.appservice.repository.OrderRepo;

@RestController
public class OrderController {
    /* 
        POST /order - response ok
        GET /orders - response pagination data
        GET /order/{id} - response ok data
        DELETE /order/{id} - response ok
    */

    @Autowired
    private OrderRepo orderRepo;

    @PostMapping("/order")
    public ResponseEntity<Response> addOrder(@RequestBody Order order) {
        return ResponseEntity.ok(Response.builder()
                .build());
    }

    @GetMapping("/orders")
    public ResponseEntity<Response> getOrders(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if(size != null && (size<10 || size>50)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size should be between 10 and 50");
        if(page != null && page<0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page should be greater than or equal to 0");
        if(size == null) size = 10;
        if(page == null) page = 0;
        List<Order> orders = orderRepo.findAll();
        if(page*size>orders.size()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page out of bounds");
        return ResponseEntity.ok(Response.builder()
                .data(orders.stream().skip(page * size).limit(size).toList())
                .page(page)
                .size(size)
                .totalPages((int) Math.ceil((double) orders.size() / size))
                .hasNext(orders.size() > (page + 1) * size)
                .hasPrevious(page > 0)
                .build());
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<Response> getOrderById(@PathVariable Integer id) {
        return ResponseEntity.ok(Response.builder()
                .data(orderRepo.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Order with id: " + id + " not found"))
                )
                .build());
    }

    @DeleteMapping("/order/{id}")
    public ResponseEntity<Response> deleteOrder(@PathVariable Integer id) {
        if(orderRepo.existsById(id)) {
            orderRepo.deleteById(id);
            return ResponseEntity.ok(Response.builder()
                    .build());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id: " + id + " not found");
        }
    }
                
}
