package com.artisan.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SellerReviewReplyRequest {

    @NotBlank
    private String sellerId;

    @NotBlank
    @Size(max = 1000)
    private String reply;
}
