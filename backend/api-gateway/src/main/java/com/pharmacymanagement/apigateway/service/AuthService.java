package com.pharmacymanagement.apigateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pharmacymanagement.apigateway.config.WebAuthConfig;

import reactor.core.publisher.Mono;

@Service
public class AuthService {

    @Autowired
    WebAuthConfig webAuthConfig;

    public Mono<Boolean> validateToken(String token) {
        return webAuthConfig.authWebClient()
                .get()
                .uri("/validateAuthToken/" + token)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(e -> Mono.empty());
    }
}