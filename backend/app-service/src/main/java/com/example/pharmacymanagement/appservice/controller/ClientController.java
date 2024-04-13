package com.example.pharmacymanagement.appservice.controller;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.pharmacymanagement.appservice.dto.Response;
import com.example.pharmacymanagement.appservice.entity.Client;
import com.example.pharmacymanagement.appservice.repository.ClientRepo;

@RestController
@RequestMapping("/client")
public class ClientController {
    /*
     * POST /client - response ok
     * GET /clients - response pagination data
     * GET /client/{id} - response ok data
     * DELETE /client/{id} - response ok
     * GET /client?name= - response paginated data
     * PATCH /client/{id} - response ok
     */

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
    private static final String PHONE_REGEX = "^[1-9]{1}[0-9]{9}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    @Autowired
    ClientRepo clientRepo;

    @PostMapping("/")
    public ResponseEntity<Response> addClient(@RequestBody Client client) {
        if (client == null || client.getEmail() == null || client.getPhone() == null ||
                client.getName() == null || client.getName().isBlank() || client.getEmail().isBlank()
                || client.getPhone().toString().length() != 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid client data");
        }
        if (!EMAIL_PATTERN.matcher(client.getEmail()).matches())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
        if (!PHONE_PATTERN.matcher(client.getPhone().toString()).matches())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number");
        if (clientRepo.existsByEmailAndPhone(client.getEmail(), client.getPhone()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client already exists");
        return ResponseEntity.ok(Response.builder()
                .data("Client added successfully")
                .build());

    }

    @GetMapping("/all")
    public ResponseEntity<Response> getClients(@RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (size != null && (size < 10 || size > 50))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size should be between 10 and 50");
        if (page != null && page < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page should be greater than or equal to 0");
        if (size == null)
            size = 10;
        if (page == null)
            page = 0;
        Page<Client> clients = clientRepo.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(Response.builder()
                .data(clients.getContent())
                .page(page)
                .size(size)
                .totalPages(clients.getTotalPages())
                .hasNext(clients.hasNext())
                .hasPrevious(clients.hasPrevious())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getClientById(@RequestParam Integer id) {
        return ResponseEntity.ok(Response.builder()
                .data(clientRepo.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Client with id: " + id + " not found")))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteClient(@RequestParam Integer id) {
        if (clientRepo.existsById(id)) {
            clientRepo.deleteById(id);
            return ResponseEntity.ok(Response.builder()
                    .build());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id: " + id + " not found");
        }
    }

    @GetMapping("/")
    public ResponseEntity<Response> searchClient(@RequestParam String name) {
        if (name.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be blank");
        if (name.length() < 3)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name should be at least 3 characters long");
        String queryName = "%" + name + "%";
        Page<Client> clients = clientRepo.findByNameLikeIgnoreCase(queryName, PageRequest.of(0, 10));
        return ResponseEntity.ok(Response.builder()
                .data(clients.getContent())
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response> updateClient(@RequestBody Client client, @RequestParam Integer id) {
        Client existingClient = clientRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client with id: " + id + " not found"));
        if (client.getName() != null && !client.getName().isBlank())
            existingClient.setName(client.getName());
        clientRepo.save(existingClient);
        return ResponseEntity.ok(Response.builder()
                .data("Client with id: " + id + " updated")
                .build());

    }
}
