package com.artisan.listing.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ReviewServiceClient {

    private final WebClient webClient;

    public ReviewServiceClient(@Value("${integration.api-gateway.url:http://localhost:8084}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public ListingReviewSummary getListingSummary(String listingId) {
        try {
            ListingReviewSummary response = webClient.get()
                    .uri("/api/reviews/summary/listing/{listingId}", listingId)
                    .retrieve()
                    .bodyToMono(ListingReviewSummary.class)
                    .block();
            return response != null ? response : new ListingReviewSummary(listingId, null, null);
        } catch (Exception e) {
            return new ListingReviewSummary(listingId, null, null);
        }
    }

    public record ListingReviewSummary(String listingId, Double averageRating, Long reviewCount) {}
}
