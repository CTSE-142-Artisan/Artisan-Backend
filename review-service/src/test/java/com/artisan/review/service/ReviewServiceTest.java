package com.artisan.review.service;

import com.artisan.review.client.ListingServiceClient;
import com.artisan.review.client.OrderServiceClient;
import com.artisan.review.client.UserServiceClient;
import com.artisan.review.dto.CreateReviewRequest;
import com.artisan.review.dto.SellerReviewReplyRequest;
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

    @Mock
    private OrderServiceClient orderServiceClient;

    @Mock
    private ListingServiceClient listingServiceClient;

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
    void createRejectsWhenListingNotPurchased() {
        CreateReviewRequest request = new CreateReviewRequest();
        request.setListingId("listing-1");
        request.setOrderId("order-1");
        request.setUserId("user-1");
        request.setRating(5);

        when(repository.findByOrderIdAndUserIdAndListingId("order-1", "user-1", "listing-1"))
                .thenReturn(Optional.empty());
        when(orderServiceClient.hasPurchased("user-1", "listing-1")).thenReturn(false);

        assertThatThrownBy(() -> reviewService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Reviews are only allowed for purchased listings");
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
        when(orderServiceClient.hasPurchased("user-1", "listing-2")).thenReturn(true);
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

    @Test
    void replyToReviewStoresSellerReplyWhenSellerOwnsListing() {
        Review review = Review.builder()
                .id("review-1")
                .listingId("listing-1")
                .orderId("order-1")
                .userId("buyer-1")
                .rating(5)
                .comment("Excellent")
                .visible(true)
                .build();
        SellerReviewReplyRequest request = new SellerReviewReplyRequest();
        request.setSellerId("seller-1");
        request.setReply("Thank you for supporting my work.");

        when(repository.findById("review-1")).thenReturn(Optional.of(review));
        when(listingServiceClient.getListingSellerId("listing-1")).thenReturn("seller-1");
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userServiceClient.getUserProfile("buyer-1"))
                .thenReturn(new UserServiceClient.UserProfile("buyer-1", "Buyer One", null));

        var response = reviewService.replyToReview("review-1", request);

        assertThat(response.getSellerReplyUserId()).isEqualTo("seller-1");
        assertThat(response.getSellerReply()).isEqualTo("Thank you for supporting my work.");
        assertThat(response.getSellerReplyCreatedAt()).isNotNull();
        assertThat(response.getSellerReplyUpdatedAt()).isNotNull();
        verify(repository).save(any(Review.class));
    }

    @Test
    void replyToReviewRejectsSellerWhoDoesNotOwnListing() {
        Review review = Review.builder()
                .id("review-1")
                .listingId("listing-1")
                .visible(true)
                .build();
        SellerReviewReplyRequest request = new SellerReviewReplyRequest();
        request.setSellerId("seller-2");
        request.setReply("Thanks");

        when(repository.findById("review-1")).thenReturn(Optional.of(review));
        when(listingServiceClient.getListingSellerId("listing-1")).thenReturn("seller-1");

        assertThatThrownBy(() -> reviewService.replyToReview("review-1", request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Only the listing seller");
    }
}
