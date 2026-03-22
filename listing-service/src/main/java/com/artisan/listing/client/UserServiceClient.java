package com.artisan.listing.client;

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

    public boolean validateSeller(String sellerId) {
        try {
            ValidationResponse response = webClient.get()
                    .uri("/api/users/{id}/validate-seller", sellerId)
                    .retrieve()
                    .bodyToMono(ValidationResponse.class)
                    .block();
            return response != null && response.valid();
        } catch (Exception e) {
            return false;
        }
    }

    private record ValidationResponse(String userId, boolean valid) {}
}
