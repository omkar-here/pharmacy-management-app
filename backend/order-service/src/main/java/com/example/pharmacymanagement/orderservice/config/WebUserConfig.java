package com.example.pharmacymanagement.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebUserConfig {

    // @Value("${user.base.url}")
    private String userBaseUrl = "user-service";

    @Bean
    @LoadBalanced
    public WebClient userWebClient() {
        return WebClient.builder()
                .baseUrl(userBaseUrl)
                .build();
    }

}
