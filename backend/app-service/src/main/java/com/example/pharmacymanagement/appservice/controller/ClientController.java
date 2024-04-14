package com.example.pharmacymanagement.appservice.controller;

import java.util.regex.Pattern;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@CrossOrigin
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

    @CrossOrigin
    @PostMapping("/add")
    public ResponseEntity<Response> addClient(@RequestBody Client clientBody) {
        if (Objects.isNull(clientBody.getEmail()) || Objects.isNull(clientBody.getPhone())
                || Objects.isNull(clientBody.getAddress()) ||
                Objects.isNull(clientBody.getName()) || clientBody.getName().isBlank()
                || clientBody.getEmail().isBlank()
                || clientBody.getAddress().isBlank()
                || clientBody.getPhone().toString().length() != 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid client data");
        }
        if (!EMAIL_PATTERN.matcher(clientBody.getEmail()).matches())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
        if (!PHONE_PATTERN.matcher(clientBody.getPhone().toString()).matches())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number");
        if (clientRepo.existsByEmailAndPhone(clientBody.getEmail(), clientBody.getPhone()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client already exists");
        clientRepo.save(Client.builder()
                .name(clientBody.getName())
                .email(clientBody.getEmail())
                .phone(clientBody.getPhone())
                .address(clientBody.getAddress())
                .build());
        return ResponseEntity.ok(Response.builder()
                .data("Client added successfully")
                .build());
    }

    @CrossOrigin
    @GetMapping("/all")
    public ResponseEntity<Response> getClients(@RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        if (size < 10 || size > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size should be between 10 and 50");
        }
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page should be greater than or equal to 0");
        }
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

    @CrossOrigin
    @GetMapping("/{id}")
    public ResponseEntity<Response> getClientById(@PathVariable Integer id) {
        return ResponseEntity.ok(Response.builder()
                .data(clientRepo.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Client with id: " + id + " not found")))
                .build());
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteClient(@PathVariable Integer id) {
        if (clientRepo.existsById(id)) {
            clientRepo.deleteById(id);
            return ResponseEntity.ok(Response.builder()
                    .build());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client with id: " + id + " not found");
        }
    }

    @CrossOrigin
    @GetMapping("/search")
    public ResponseEntity<Response> searchClient(@RequestParam String name) {
        if (name.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be blank");
        if (name.length() < 3)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name should be at least 3 characters long");
        String queryName = "%" + name + "%";
        Slice<Client> clients = clientRepo.findByNameLikeIgnoreCase(queryName, PageRequest.of(0, 10));
        return ResponseEntity.ok(Response.builder()
                .data(clients.getContent())
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response> updateClient(@RequestBody Client clientBody, @PathVariable Integer id) {
        Client existingClient = clientRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client with id: " + id + " not found"));
        if (!Objects.isNull(clientBody.getName()) && !clientBody.getName().isBlank())
            existingClient.setName(clientBody.getName());
        if (!Objects.isNull(clientBody.getAddress()) && !clientBody.getAddress().isBlank())
            existingClient.setAddress(clientBody.getAddress());
        clientRepo.save(existingClient);
        return ResponseEntity.ok(Response.builder()
                .data("Client with id: " + id + " updated")
                .build());

    }
}
