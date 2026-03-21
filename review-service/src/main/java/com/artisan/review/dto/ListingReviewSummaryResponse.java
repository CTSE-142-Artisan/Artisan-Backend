package com.artisan.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingReviewSummaryResponse {
    private String listingId;
    private Double averageRating;
    private long reviewCount;
}
