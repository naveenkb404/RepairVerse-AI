package com.repairverse.ai.service;

import com.repairverse.ai.dto.MarketplaceDto.CreateReviewRequest;
import com.repairverse.ai.dto.MarketplaceDto.RepairReviewResponse;
import com.repairverse.ai.dto.MarketplaceDto.ShopReputationResponse;
import com.repairverse.ai.entity.Booking;
import com.repairverse.ai.entity.RepairReview;
import com.repairverse.ai.entity.RepairShop;
import com.repairverse.ai.entity.User;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.BookingRepository;
import com.repairverse.ai.repository.RepairReviewRepository;
import com.repairverse.ai.repository.RepairShopProfileRepository;
import com.repairverse.ai.repository.RepairShopRepository;
import com.repairverse.ai.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairReputationServiceTest {

    @Mock
    private RepairReviewRepository reviewRepository;
    @Mock
    private RepairShopRepository repairShopRepository;
    @Mock
    private RepairShopProfileRepository profileRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RepairReputationService reputationService;

    private RepairShop testShop;
    private User testUser;

    @BeforeEach
    void setUp() {
        testShop = RepairShop.builder().id("shop-1").shopName("iFix Hub").rating(4.8).build();
        testUser = User.builder().id("usr-1").fullName("Alex Doe").build();
    }

    @Test
    @DisplayName("Submits review successfully when user has a verified booking")
    void testSubmitReviewWithVerifiedBooking() {
        when(repairShopRepository.findById("shop-1")).thenReturn(Optional.of(testShop));
        when(userRepository.findById("usr-1")).thenReturn(Optional.of(testUser));

        Booking booking = Booking.builder().id("book-1").userId("usr-1").shopId("shop-1").build();
        when(bookingRepository.findByIdAndUserId("book-1", "usr-1")).thenReturn(Optional.of(booking));
        when(reviewRepository.existsByUserIdAndRepairShopIdAndBookingId("usr-1", "shop-1", "book-1")).thenReturn(false);
        when(reviewRepository.save(any(RepairReview.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateReviewRequest req = new CreateReviewRequest("book-1", 5, "Great Service", "Very quick display fix", 5, 5, 5, 5);
        RepairReviewResponse res = reputationService.submitReview("shop-1", req, "usr-1");

        assertThat(res.rating()).isEqualTo(5);
        assertThat(res.verifiedRepair()).isTrue();
        assertThat(res.userFullName()).isEqualTo("Alex Doe");
    }

    @Test
    @DisplayName("Throws AccessDeniedException when user has no verified booking with the shop")
    void testSubmitReviewWithoutBookingThrowsAccessDenied() {
        when(repairShopRepository.findById("shop-1")).thenReturn(Optional.of(testShop));
        when(userRepository.findById("usr-1")).thenReturn(Optional.of(testUser));
        when(bookingRepository.findByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(Collections.emptyList());

        CreateReviewRequest req = new CreateReviewRequest(null, 5, "Nice", "Good", 5, 5, 5, 5);

        assertThatThrownBy(() -> reputationService.submitReview("shop-1", req, "usr-1"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Verified completed repair booking required");
    }

    @Test
    @DisplayName("Prevents duplicate reviews for the same booking")
    void testPreventDuplicateReviews() {
        when(repairShopRepository.findById("shop-1")).thenReturn(Optional.of(testShop));
        when(userRepository.findById("usr-1")).thenReturn(Optional.of(testUser));

        Booking booking = Booking.builder().id("book-1").userId("usr-1").shopId("shop-1").build();
        when(bookingRepository.findByIdAndUserId("book-1", "usr-1")).thenReturn(Optional.of(booking));
        when(reviewRepository.existsByUserIdAndRepairShopIdAndBookingId("usr-1", "shop-1", "book-1")).thenReturn(true);

        CreateReviewRequest req = new CreateReviewRequest("book-1", 5, "Duplicate", "Comment", 5, 5, 5, 5);

        assertThatThrownBy(() -> reputationService.submitReview("shop-1", req, "usr-1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate review");
    }

    @Test
    @DisplayName("Calculates rating distribution and dimensional metrics accurately")
    void testGetShopReputation() {
        when(repairShopRepository.findById("shop-1")).thenReturn(Optional.of(testShop));

        RepairReview r1 = RepairReview.builder().rating(5).repairQualityRating(5).communicationRating(4).valueRating(5).timelinessRating(5).build();
        RepairReview r2 = RepairReview.builder().rating(4).repairQualityRating(4).communicationRating(5).valueRating(4).timelinessRating(4).build();
        when(reviewRepository.findByRepairShopIdOrderByCreatedAtDesc("shop-1")).thenReturn(List.of(r1, r2));

        ShopReputationResponse rep = reputationService.getShopReputation("shop-1");

        assertThat(rep.totalReviews()).isEqualTo(2);
        assertThat(rep.averageRating()).isEqualTo(4.5);
        assertThat(rep.ratingDistribution().get(5)).isEqualTo(1L);
        assertThat(rep.ratingDistribution().get(4)).isEqualTo(1L);
    }
}
