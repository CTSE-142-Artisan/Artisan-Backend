package com.artisan.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(@Value("${integration.api-gateway.url:http://localhost:8084}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public boolean validateUser(String userId) {
        return Boolean.TRUE.equals(webClient.get()
                .uri("/api/users/{id}/validate", userId)
                .retrieve()
                .bodyToMono(ValidationResponse.class)
                .map(r -> r != null && r.valid())
                .onErrorReturn(false)
                .block());
    }

    private record ValidationResponse(String userId, boolean valid) {}
}
