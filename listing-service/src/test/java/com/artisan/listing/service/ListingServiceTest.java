package com.artisan.listing.service;

import com.artisan.listing.client.ReviewServiceClient;
import com.artisan.listing.client.UserServiceClient;
import com.artisan.listing.dto.UpdateListingRequest;
import com.artisan.listing.model.Listing;
import com.artisan.listing.repository.ListingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private ListingRepository repository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ReviewServiceClient reviewServiceClient;

    @InjectMocks
    private ListingService listingService;

    @Test
    void updateChangesSellerOwnedListing() {
        Listing listing = Listing.builder()
                .id("listing-1")
                .sellerId("seller-1")
                .title("Old title")
                .description("Old description")
                .category("jewelry")
                .country("Sri Lanka")
                .imageUrls(List.of("https://example.com/old.jpg"))
                .price(new BigDecimal("12.50"))
                .currency("USD")
                .stockQuantity(2)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        UpdateListingRequest request = new UpdateListingRequest();
        request.setSellerId("seller-1");
        request.setTitle("New title");
        request.setDescription("New description");
        request.setCategory("pottery");
        request.setCountry("India");
        request.setImageUrls(List.of("https://example.com/new.jpg"));
        request.setPrice(new BigDecimal("24.00"));
        request.setCurrency("EUR");
        request.setStockQuantity(5);

        when(repository.findById("listing-1")).thenReturn(Optional.of(listing));
        when(repository.save(any(Listing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = listingService.update("listing-1", request);

        assertThat(response.getTitle()).isEqualTo("New title");
        assertThat(response.getCategory()).isEqualTo("pottery");
        assertThat(response.getPrice()).isEqualByComparingTo("24.00");
        assertThat(response.getStockQuantity()).isEqualTo(5);
        verify(repository).save(listing);
    }

    @Test
    void updateRejectsSellerWhoDoesNotOwnListing() {
        Listing listing = Listing.builder()
                .id("listing-1")
                .sellerId("seller-1")
                .build();
        UpdateListingRequest request = new UpdateListingRequest();
        request.setSellerId("seller-2");
        request.setTitle("New title");
        request.setCategory("pottery");
        request.setPrice(new BigDecimal("24.00"));

        when(repository.findById("listing-1")).thenReturn(Optional.of(listing));

        assertThatThrownBy(() -> listingService.update("listing-1", request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Only the listing seller");
    }

    @Test
    void deleteMarksSellerOwnedListingInactive() {
        Listing listing = Listing.builder()
                .id("listing-1")
                .sellerId("seller-1")
                .active(true)
                .build();

        when(repository.findById("listing-1")).thenReturn(Optional.of(listing));
        when(repository.save(any(Listing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        listingService.delete("listing-1", "seller-1");

        ArgumentCaptor<Listing> captor = ArgumentCaptor.forClass(Listing.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();
        assertThat(captor.getValue().getUpdatedAt()).isNotNull();
    }
}
