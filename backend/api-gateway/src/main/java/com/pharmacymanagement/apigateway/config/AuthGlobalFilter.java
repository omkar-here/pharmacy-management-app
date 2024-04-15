package com.pharmacymanagement.apigateway.config;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.pharmacymanagement.apigateway.dto.AuthToken;

import reactor.core.publisher.Mono;

@Component
public class AuthGlobalFilter implements GlobalFilter {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getURI().getPath().contains("auth")) {
            return chain.filter(exchange);
        }
        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is invalid");
        }
        List<String> headers = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        String authHeader = !Objects.isNull(headers) && headers.size() > 0 ? headers.get(0) : null;
        if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is invalid");
        }
        String token = authHeader.substring(7);

        return webClientBuilder.build()
                .post()
                .uri("http://user-service/auth/internal/jwtFields/" + token)
                .retrieve().bodyToMono(AuthToken.class)
                .map(authToken -> {
                    if (Objects.isNull(authToken)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User token expired");
                    }
                    exchange.getRequest()
                            .mutate()
                            .header("employeeId", String.valueOf(authToken.getId()));
                    exchange.getRequest()
                            .mutate()
                            .header("employeeRole", authToken.getRole().toString());
                    return exchange;
                }).flatMap(chain::filter);
    }
}