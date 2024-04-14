package com.pharmacymanagement.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebAuthConfig {

    private LoadBalancedExchangeFilterFunction lbFunction;

    @Value("${auth.base.url}")
    private String authBaseUrl;

    @Bean
    public
    WebClient authWebClient() {
        return WebClient.builder()
                .baseUrl(authBaseUrl)
                .filter(lbFunction)
                .build();
    }

}
