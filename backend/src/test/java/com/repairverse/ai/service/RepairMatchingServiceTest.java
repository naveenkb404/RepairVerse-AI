package com.repairverse.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.MarketplaceDto.TrustScoreResponse;
import com.repairverse.ai.dto.RepairMatchingDto.RepairShopMatchResponse;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.RepairShop;
import com.repairverse.ai.entity.RepairShopProfile;
import com.repairverse.ai.entity.RepairShopSpecialization;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairMatchingServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;

    @Mock
    private RepairShopRepository repairShopRepository;

    @Mock
    private RepairShopProfileRepository profileRepository;

    @Mock
    private RepairShopSpecializationRepository specializationRepository;

    @Mock
    private RepairMatchHistoryRepository matchHistoryRepository;

    @Mock
    private RepairTrustService trustService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private RepairMatchingService matchingService;

    private Device sampleDevice;
    private RepairShop sampleShop1;
    private RepairShop sampleShop2;

    @BeforeEach
    void setUp() {
        sampleDevice = Device.builder()
                .id("dev-1")
                .userId("usr-1")
                .deviceName("MacBook Pro M2")
                .category("Laptop")
                .brand("Apple")
                .build();

        sampleShop1 = RepairShop.builder()
                .id("shop-1")
                .shopName("Apple Silicon Masters")
                .address("100 Tech Way")
                .latitude(37.77)
                .longitude(-122.41)
                .rating(4.9)
                .reviewCount(150)
                .servicesJson("[\"Laptop Repair\",\"Screen Replacement\",\"Logic Board Repair\"]")
                .serviceCategoriesJson("[\"Laptop\"]")
                .certifiedBrandsJson("[\"Apple\"]")
                .estimatedTurnaround("Same Day (2-4 hrs)")
                .avgPrice("$85")
                .verified(true)
                .isOpen(true)
                .ecoCertified(true)
                .build();

        sampleShop2 = RepairShop.builder()
                .id("shop-2")
                .shopName("Budget Phone Clinic")
                .address("50 Market St")
                .latitude(37.78)
                .longitude(-122.42)
                .rating(4.2)
                .reviewCount(30)
                .servicesJson("[\"Smartphone Repair\"]")
                .serviceCategoriesJson("[\"Smartphone\"]")
                .certifiedBrandsJson("[\"Samsung\"]")
                .estimatedTurnaround("3-5 Days")
                .avgPrice("$45")
                .verified(false)
                .isOpen(false)
                .ecoCertified(false)
                .build();
    }

    @Test
    @DisplayName("findMatchesForDevice — ranks specialist higher with deterministic scores")
    void testFindMatchesForDevice() {
        when(deviceRepository.findById("dev-1")).thenReturn(Optional.of(sampleDevice));
        when(repairShopRepository.findAll()).thenReturn(List.of(sampleShop1, sampleShop2));

        RepairShopProfile profile1 = RepairShopProfile.builder()
                .repairShopId("shop-1")
                .verificationStatus("TRUSTED")
                .verificationLevel("PREMIUM")
                .yearsOfExperience(8)
                .totalRepairsCompleted(600)
                .warrantyDays(180)
                .averageResponseTimeMinutes(15)
                .build();

        when(profileRepository.findByRepairShopId("shop-1")).thenReturn(Optional.of(profile1));
        when(profileRepository.findByRepairShopId("shop-2")).thenReturn(Optional.empty());

        when(specializationRepository.findByRepairShopId("shop-1")).thenReturn(List.of(
                RepairShopSpecialization.builder()
                        .repairShopId("shop-1")
                        .deviceCategory("Laptop")
                        .brand("Apple")
                        .specializationLevel("EXPERT")
                        .build()
        ));
        when(specializationRepository.findByRepairShopId("shop-2")).thenReturn(List.of());

        when(trustService.evaluateTrust("shop-1")).thenReturn(new TrustScoreResponse("shop-1", 95, "EXCEPTIONAL", List.of(), List.of(), List.of(), false));
        when(trustService.evaluateTrust("shop-2")).thenReturn(new TrustScoreResponse("shop-2", 60, "LIMITED", List.of(), List.of(), List.of(), false));

        List<RepairShopMatchResponse> results = matchingService.findMatchesForDevice(
                "dev-1", "usr-1", 37.77, -122.41, null, "Logic Board Repair");

        assertNotNull(results);
        assertEquals(2, results.size());

        RepairShopMatchResponse topMatch = results.get(0);
        assertEquals("shop-1", topMatch.shopId());
        assertEquals(1, topMatch.rank());
        assertTrue(topMatch.overallScore() >= 80, "Specialist score should be >= 80");
        assertEquals("EXCELLENT_MATCH", topMatch.matchLevel());
        assertTrue(topMatch.isEcoCertified());
        assertNotNull(topMatch.explanation());
        assertFalse(topMatch.factors().isEmpty());

        RepairShopMatchResponse secondMatch = results.get(1);
        assertEquals("shop-2", secondMatch.shopId());
        assertEquals(2, secondMatch.rank());
        assertTrue(topMatch.overallScore() > secondMatch.overallScore(), "Shop 1 score must exceed Shop 2");
    }

    @Test
    @DisplayName("findMatchesForDevice — handles missing coordinates gracefully")
    void testFindMatchesWithoutCoordinates() {
        when(deviceRepository.findById("dev-1")).thenReturn(Optional.of(sampleDevice));
        when(repairShopRepository.findAll()).thenReturn(List.of(sampleShop1));
        when(trustService.evaluateTrust("shop-1")).thenReturn(new TrustScoreResponse("shop-1", 90, "EXCEPTIONAL", List.of(), List.of(), List.of(), false));

        List<RepairShopMatchResponse> results = matchingService.findMatchesForDevice(
                "dev-1", "usr-1", null, null, null, null);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertNull(results.get(0).distanceKm(), "Distance should be null when coordinates are not passed");
        assertTrue(results.get(0).overallScore() > 0);
    }
}
