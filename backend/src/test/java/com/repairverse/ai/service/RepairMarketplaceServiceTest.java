package com.repairverse.ai.service;

import com.repairverse.ai.dto.MarketplaceDto.MarketplaceShopResponse;
import com.repairverse.ai.dto.MarketplaceDto.ShopRankingResponse;
import com.repairverse.ai.entity.RepairShop;
import com.repairverse.ai.entity.RepairShopProfile;
import com.repairverse.ai.entity.RepairShopSpecialization;
import com.repairverse.ai.repository.RepairShopProfileRepository;
import com.repairverse.ai.repository.RepairShopRepository;
import com.repairverse.ai.repository.RepairShopSpecializationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairMarketplaceServiceTest {

    @Mock
    private RepairShopRepository repairShopRepository;
    @Mock
    private RepairShopProfileRepository profileRepository;
    @Mock
    private RepairShopSpecializationRepository specializationRepository;
    @Mock
    private RepairTrustService trustService;

    @InjectMocks
    private RepairMarketplaceService marketplaceService;

    private RepairShop shopA;
    private RepairShop shopB;

    @BeforeEach
    void setUp() {
        shopA = RepairShop.builder()
                .id("shop-a")
                .shopName("Alpha Tech Hub")
                .rating(4.9)
                .reviewCount(50)
                .build();

        shopB = RepairShop.builder()
                .id("shop-b")
                .shopName("Beta Basic Repair")
                .rating(4.0)
                .reviewCount(10)
                .build();
    }

    @Test
    @DisplayName("Discovers and ranks shops in descending order of deterministic marketplace score")
    void testDiscoverAndRankShops() {
        when(repairShopRepository.findAll()).thenReturn(List.of(shopA, shopB));

        RepairShopProfile profileA = RepairShopProfile.builder()
                .repairShopId("shop-a")
                .verificationStatus("TRUSTED")
                .verificationLevel("PREMIUM")
                .averageRating(4.9)
                .totalReviews(50)
                .responseRate(98.0)
                .warrantyDays(180)
                .yearsOfExperience(8)
                .build();

        RepairShopProfile profileB = RepairShopProfile.builder()
                .repairShopId("shop-b")
                .verificationStatus("PENDING")
                .verificationLevel("BASIC")
                .averageRating(4.0)
                .totalReviews(10)
                .responseRate(80.0)
                .warrantyDays(30)
                .yearsOfExperience(2)
                .build();

        when(profileRepository.findByRepairShopId("shop-a")).thenReturn(Optional.of(profileA));
        when(profileRepository.findByRepairShopId("shop-b")).thenReturn(Optional.of(profileB));
        when(specializationRepository.findByRepairShopId("shop-a")).thenReturn(Collections.emptyList());
        when(specializationRepository.findByRepairShopId("shop-b")).thenReturn(Collections.emptyList());

        List<MarketplaceShopResponse> results = marketplaceService.discoverShops(
                null, null, null, null, null, null, null);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).id()).isEqualTo("shop-a"); // Ranked first
        assertThat(results.get(0).marketplaceScore()).isGreaterThan(results.get(1).marketplaceScore());
    }

    @Test
    @DisplayName("Filters shops by minimum rating")
    void testFilterByMinRating() {
        when(repairShopRepository.findAll()).thenReturn(List.of(shopA, shopB));

        RepairShopProfile profileA = RepairShopProfile.builder().repairShopId("shop-a").averageRating(4.9).verificationStatus("VERIFIED").build();
        RepairShopProfile profileB = RepairShopProfile.builder().repairShopId("shop-b").averageRating(4.0).verificationStatus("VERIFIED").build();

        when(profileRepository.findByRepairShopId("shop-a")).thenReturn(Optional.of(profileA));
        when(profileRepository.findByRepairShopId("shop-b")).thenReturn(Optional.of(profileB));

        List<MarketplaceShopResponse> results = marketplaceService.discoverShops(
                null, null, 4.5, null, null, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).id()).isEqualTo("shop-a");
    }

    @Test
    @DisplayName("Calculates transparent ranking breakdown with explainable reasons")
    void testGetShopRankingBreakdown() {
        when(repairShopRepository.findById("shop-a")).thenReturn(Optional.of(shopA));

        RepairShopProfile profileA = RepairShopProfile.builder()
                .repairShopId("shop-a")
                .verificationStatus("TRUSTED")
                .averageRating(4.9)
                .totalReviews(50)
                .yearsOfExperience(5)
                .responseRate(98.0)
                .warrantyDays(90)
                .build();

        when(profileRepository.findByRepairShopId("shop-a")).thenReturn(Optional.of(profileA));
        when(specializationRepository.findByRepairShopId("shop-a")).thenReturn(List.of(
                RepairShopSpecialization.builder().deviceCategory("Smartphone").brand("Apple").build()
        ));

        ShopRankingResponse ranking = marketplaceService.getShopRanking("shop-a", "Smartphone", "Apple");

        assertThat(ranking.totalScore()).isGreaterThan(75);
        assertThat(ranking.rankingReasons()).isNotEmpty();
        assertThat(ranking.strengths()).isNotEmpty();
    }
}
