package com.repairverse.ai.service;

import com.repairverse.ai.dto.MarketplaceDto.TrustScoreResponse;
import com.repairverse.ai.entity.RepairShop;
import com.repairverse.ai.entity.RepairShopProfile;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.RepairReviewRepository;
import com.repairverse.ai.repository.RepairShopProfileRepository;
import com.repairverse.ai.repository.RepairShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairTrustServiceTest {

    @Mock
    private RepairShopRepository repairShopRepository;
    @Mock
    private RepairShopProfileRepository profileRepository;
    @Mock
    private RepairReviewRepository reviewRepository;

    @InjectMocks
    private RepairTrustService trustService;

    private RepairShop testShop;

    @BeforeEach
    void setUp() {
        testShop = RepairShop.builder()
                .id("shop-1")
                .shopName("FixVerse Premier Hub")
                .rating(4.9)
                .reviewCount(85)
                .build();
    }

    @Test
    @DisplayName("Calculates EXCEPTIONAL trust score (>=85) for premium verified shop with high rating")
    void testExceptionalTrustScore() {
        when(repairShopRepository.findById("shop-1")).thenReturn(Optional.of(testShop));

        RepairShopProfile profile = RepairShopProfile.builder()
                .repairShopId("shop-1")
                .verificationStatus("TRUSTED")
                .verificationLevel("PREMIUM")
                .averageRating(4.9)
                .totalReviews(85)
                .totalRepairsCompleted(150)
                .warrantyDays(180)
                .responseRate(98.0)
                .build();
        when(profileRepository.findByRepairShopId("shop-1")).thenReturn(Optional.of(profile));

        TrustScoreResponse res = trustService.evaluateTrust("shop-1");

        assertThat(res.trustScore()).isGreaterThanOrEqualTo(85);
        assertThat(res.trustLevel()).isEqualTo("EXCEPTIONAL");
        assertThat(res.positiveSignals()).isNotEmpty();
    }

    @Test
    @DisplayName("Applies penalty and flags risk signals for suspended shop")
    void testSuspendedShopTrustScore() {
        when(repairShopRepository.findById("shop-1")).thenReturn(Optional.of(testShop));

        RepairShopProfile profile = RepairShopProfile.builder()
                .repairShopId("shop-1")
                .verificationStatus("SUSPENDED")
                .averageRating(3.2)
                .totalReviews(10)
                .totalRepairsCompleted(5)
                .warrantyDays(30)
                .responseRate(60.0)
                .build();
        when(profileRepository.findByRepairShopId("shop-1")).thenReturn(Optional.of(profile));

        TrustScoreResponse res = trustService.evaluateTrust("shop-1");

        assertThat(res.trustScore()).isLessThan(50);
        assertThat(res.riskSignals()).anyMatch(s -> s.contains("suspended") || s.contains("Suspended"));
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException for invalid shop ID")
    void testInvalidShopThrowsException() {
        when(repairShopRepository.findById("shop-invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trustService.evaluateTrust("shop-invalid"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
