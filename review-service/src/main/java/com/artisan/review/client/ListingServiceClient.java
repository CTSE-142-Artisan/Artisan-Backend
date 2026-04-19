package com.artisan.review.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ListingServiceClient {

    private final WebClient webClient;

    public ListingServiceClient(@Value("${integration.api-gateway.url:http://localhost:8084}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public String getListingSellerId(String listingId) {
        try {
            ListingInfo response = webClient.get()
                    .uri("/api/listings/{id}", listingId)
                    .retrieve()
                    .bodyToMono(ListingInfo.class)
                    .block();
            return response != null ? response.sellerId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private record ListingInfo(String id, String sellerId) {}
}
