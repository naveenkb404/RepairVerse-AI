package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairMatchingDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmartRepairRecommendationServiceTest {

    @Mock
    private RepairMatchingService matchingService;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private SmartRepairRecommendationService recommendationService;

    private RepairShopMatchResponse shop1;
    private RepairShopMatchResponse shop2;
    private RepairShopMatchResponse shop3;

    @BeforeEach
    void setUp() {
        shop1 = new RepairShopMatchResponse(
                "shop-1", "Apex Tech", "123 Main", 37.77, -122.41,
                "+1 555", "info@apex.com", "9-5", 4.9, 120, "TRUSTED", "PREMIUM",
                1.5, 94, "EXCELLENT_MATCH", 1, List.of(),
                new RepairMatchExplanation("Summary", List.of(), "EXCELLENT_MATCH", List.of()),
                85.0, 4.0, 180, 96, true, false
        );

        shop2 = new RepairShopMatchResponse(
                "shop-2", "Value Fix", "456 Side", 37.78, -122.42,
                "+1 555", "info@value.com", "9-5", 4.6, 60, "VERIFIED", "BASIC",
                4.2, 78, "GREAT_MATCH", 2, List.of(),
                new RepairMatchExplanation("Summary", List.of(), "GREAT_MATCH", List.of()),
                45.0, 24.0, 90, 82, false, false
        );

        shop3 = new RepairShopMatchResponse(
                "shop-3", "Eco Circuit", "789 Green", 37.79, -122.43,
                "+1 555", "info@eco.com", "9-5", 4.8, 90, "TRUSTED", "VERIFIED",
                8.0, 88, "EXCELLENT_MATCH", 3, List.of(),
                new RepairMatchExplanation("Summary", List.of(), "EXCELLENT_MATCH", List.of()),
                70.0, 12.0, 120, 92, true, false
        );
    }

    @Test
    @DisplayName("getRecommendationsForDevice — returns categorized recommendations")
    void testGetRecommendations() {
        Device device = Device.builder().id("dev-1").deviceName("iPhone 14").category("Smartphone").build();
        when(deviceRepository.findById("dev-1")).thenReturn(Optional.of(device));
        when(matchingService.findMatchesForDevice(eq("dev-1"), any(), any(), any(), any(), any()))
                .thenReturn(List.of(shop1, shop2, shop3));

        SmartRecommendationResponse response = recommendationService.getRecommendationsForDevice(
                "dev-1", "usr-1", 37.77, -122.41, null, null);

        assertNotNull(response);
        assertEquals("dev-1", response.deviceId());
        assertEquals("iPhone 14", response.deviceName());
        assertEquals(3, response.totalEvaluated());

        List<CategoryRecommendation> recs = response.recommendations();
        assertFalse(recs.isEmpty());

        // Verify categories exist
        assertTrue(recs.stream().anyMatch(r -> "BEST_OVERALL".equals(r.category())));
        assertTrue(recs.stream().anyMatch(r -> "BEST_VALUE".equals(r.category())));
        assertTrue(recs.stream().anyMatch(r -> "FASTEST_REPAIR".equals(r.category())));
        assertTrue(recs.stream().anyMatch(r -> "MOST_TRUSTED".equals(r.category())));
        assertTrue(recs.stream().anyMatch(r -> "MOST_SUSTAINABLE".equals(r.category())));

        CategoryRecommendation bestOverall = recs.stream().filter(r -> "BEST_OVERALL".equals(r.category())).findFirst().orElseThrow();
        assertEquals("shop-1", bestOverall.shop().shopId());

        CategoryRecommendation bestValue = recs.stream().filter(r -> "BEST_VALUE".equals(r.category())).findFirst().orElseThrow();
        assertEquals("shop-2", bestValue.shop().shopId());
    }

    @Test
    @DisplayName("compareShops — compares selected shops with deterministic metric matrix")
    void testCompareShops() {
        when(matchingService.findMatchesForDevice(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(shop1, shop2, shop3));

        RepairMarketplaceComparison comparison = recommendationService.compareShops(
                List.of("shop-1", "shop-2"), "dev-1", "usr-1", 37.77, -122.41);

        assertNotNull(comparison);
        assertEquals(2, comparison.shops().size());
        assertEquals("shop-1", comparison.bestOverallShopId());
        assertEquals("shop-2", comparison.bestValueShopId());
        assertFalse(comparison.metrics().isEmpty());
    }
}
