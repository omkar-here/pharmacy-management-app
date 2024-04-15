package com.example.pharmacymanagement.orderservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pharmacymanagement.orderservice.config.WebUserConfig;

import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    WebUserConfig webUserConfig;

    public Mono<Boolean> findClientById(Integer id) {
        return webUserConfig.userWebClient()
                .get()
                .uri("client/internal/exists/" + id)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(e -> Mono.empty());
    }
}