package com.artisan.review.service;

import com.artisan.review.client.UserServiceClient;
import com.artisan.review.dto.CreateReviewRequest;
import com.artisan.review.model.Review;
import com.artisan.review.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository repository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void createRejectsDuplicateReviewForSameListingInOrder() {
        CreateReviewRequest request = new CreateReviewRequest();
        request.setListingId("listing-1");
        request.setOrderId("order-1");
        request.setUserId("user-1");
        request.setRating(5);
        request.setComment("Excellent");

        when(repository.findByOrderIdAndUserIdAndListingId("order-1", "user-1", "listing-1"))
                .thenReturn(Optional.of(new Review()));

        assertThatThrownBy(() -> reviewService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Review already exists");
    }

    @Test
    void createAllowsDifferentListingWithinSameOrder() {
        CreateReviewRequest request = new CreateReviewRequest();
        request.setListingId("listing-2");
        request.setOrderId("order-1");
        request.setUserId("user-1");
        request.setRating(4);
        request.setComment("Nicely made");

        when(repository.findByOrderIdAndUserIdAndListingId("order-1", "user-1", "listing-2"))
                .thenReturn(Optional.empty());
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userServiceClient.getUserProfile("user-1"))
                .thenReturn(new UserServiceClient.UserProfile("user-1", "Buyer One", null));

        var response = reviewService.create(request);

        assertThat(response.getListingId()).isEqualTo("listing-2");
        assertThat(response.getOrderId()).isEqualTo("order-1");
        assertThat(response.getUserId()).isEqualTo("user-1");
        assertThat(response.getRating()).isEqualTo(4);
        verify(repository).save(any(Review.class));
    }
}
