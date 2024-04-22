package com.pharmacymanagement.apigateway.config;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.pharmacymanagement.apigateway.service.UserService;

import reactor.core.publisher.Mono;

@Component
public class AuthGlobalFilter implements GlobalFilter {

    @Autowired
    private UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (exchange.getRequest().getURI().getPath().contains("auth")) {
            return chain.filter(exchange);
        } else if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is invalid");
        }
        List<String> headers = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        String authHeader = !Objects.isNull(headers) && headers.size() > 0 ? headers.get(0) : null;
        if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is invalid");
        }
        String token = authHeader.substring(7);
        if (token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header is invalid");
        }
        System.out.println("Token: " + token);
        return userService.getJwtFields(token)
                .flatMap(authToken -> {
                    System.out.println("AuthToken: " + authToken);
                    if (Objects.isNull(authToken)) {
                        this.test("Invalid token");
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token"));
                    } else {
                        this.test(authToken);
                        ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(exchange.getRequest().mutate()
                                        .header("x-employee-id", String.valueOf(authToken.getId()))
                                        .header("x-employee-role", authToken.getRole().toString())
                                        .build())
                                .build();
                        this.test("Valid token");
                        return chain.filter(mutatedExchange);
                    }
                })
                .onErrorResume((e) -> {
                    this.test("Error: " + e);
                    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token"));
                });

        // userService.getJwtFields(token).subscribe(
        // value -> {
        // System.out.println("Value: " + value);
        // if (value == null) {
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token");
        // }
        // exchange.getRequest().mutate().header("x-employee-id",
        // value.getId().toString()).build();
        // exchange.getRequest().mutate().header("x-employee-role",
        // value.getRole().toString()).build();
        // },
        // error -> {
        // System.out.println("Error: " + error);
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token");
        // });
        // return chain.filter(exchange);

    }

    private void test(Object string) {
        System.out.println(string);
    }
}