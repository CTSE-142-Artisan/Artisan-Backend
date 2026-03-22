package com.artisan.listing.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListingResponse {
    private String id;
    private String sellerId;
    private String title;
    private String description;
    private String category;
    private String country;
    private List<String> imageUrls;
    private BigDecimal price;
    private String currency;
    private int stockQuantity;
    private boolean active;
    private Instant createdAt;
    private Double averageRating;
    private Long reviewCount;
}
