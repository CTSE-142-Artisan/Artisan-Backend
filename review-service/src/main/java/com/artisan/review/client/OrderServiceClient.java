package com.artisan.review.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OrderServiceClient {

    private final WebClient webClient;

    public OrderServiceClient(@Value("${integration.order-service.url:http://localhost:8082}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public boolean hasPurchased(String buyerId, String listingId) {
        try {
            PurchaseVerificationResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/orders/verify-purchase")
                            .queryParam("buyerId", buyerId)
                            .queryParam("listingId", listingId)
                            .build())
                    .retrieve()
                    .bodyToMono(PurchaseVerificationResponse.class)
                    .block();
            return response != null && response.purchased();
        } catch (Exception e) {
            return false;
        }
    }

    private record PurchaseVerificationResponse(String buyerId, String listingId, boolean purchased) {}
}
