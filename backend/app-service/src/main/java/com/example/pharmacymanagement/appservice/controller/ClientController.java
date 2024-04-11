package com.example.pharmacymanagement.appservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.pharmacymanagement.appservice.dto.Response;
import com.example.pharmacymanagement.appservice.entity.Client;

@RestController
public class ClientController {
    /*
        POST /client - response ok
        GET /clients - response pagination data
        GET /client/{id} - response ok data
        DELETE /client/{id} - response ok
        GET /client/{id}/orders - response ok data
        GET /client?name= - response paginated data
        PATCH /client/{id} - response ok
    */

    @PostMapping("/client")
    public ResponseEntity<Response> addClient(@RequestBody Client client) {
        return ResponseEntity.ok(Response.builder()
                .build());
    }

    
}
