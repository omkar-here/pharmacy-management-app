package com.example.pharmacymanagement.appservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;

@Configuration
public class WebClientConfig {

    private LoadBalancedExchangeFilterFunction lbFunction;

    @Bean
    WebClient employWebClient() {
        return WebClient.builder()
                .baseUrl("http://auth-service")
                .filter(lbFunction)
                .build();
    }

    @Bean
    employeeClient employeeClient() {
        HttpServiceProxyFactory factory = new HttpServiceProxyFactory.builder(
                WebClientAdapter.forClient(employeeWebClient()))
                .build();
        return factory.createProxy(employeeClient.class);
    }

}
