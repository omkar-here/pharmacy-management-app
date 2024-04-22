package com.pharmacymanagement.apigateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.pharmacymanagement.apigateway.dto.AuthToken;

@Service
public class UserService {
    @Autowired
    private WebClient.Builder webClientBuilder;

    public AuthToken getJwtFields(String token) {
        return webClientBuilder.build()
                .post()
                .uri("http://user-service/auth/internal/jwtFields/" + token)
                .retrieve().bodyToMono(AuthToken.class)
                .block();
    }
}
