package com.repairverse.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.ShopDto.*;
import com.repairverse.ai.entity.RepairShop;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.RepairShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairShopServiceTest {

    @Mock
    private RepairShopRepository repairShopRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private RepairShopService repairShopService;

    @Test
    @DisplayName("Should throw IllegalArgumentException when latitude is invalid (< -90)")
    void getShops_InvalidLatitude() {
        assertThatThrownBy(() -> repairShopService.getShops(-95.0, 77.0, 10.0, null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Latitude must be between -90 and 90 degrees");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when longitude is invalid (> 180)")
    void getShops_InvalidLongitude() {
        assertThatThrownBy(() -> repairShopService.getShops(12.0, 185.0, 10.0, null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Longitude must be between -180 and 180 degrees");
    }

    @Test
    @DisplayName("Should return sample shops when database has no repair shops")
    void getShops_ReturnsSampleShopsWhenDbEmpty() {
        when(repairShopRepository.findAll()).thenReturn(Collections.emptyList());

        ShopListResponse response = repairShopService.getShops(37.7749, -122.4194, 25.0, null, null, null, null, "nearest");

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isNotEmpty();
        assertThat(response.data().get(0).isDemo()).isTrue();
    }

    @Test
    @DisplayName("Should calculate Haversine distance correctly")
    void calculateHaversineDistance() {
        // Distance between SF (37.7749, -122.4194) and Palo Alto (37.4419, -122.1430) is approx 41-43 km
        double distance = RepairShopService.calculateHaversineDistance(37.7749, -122.4194, 37.4419, -122.1430);
        assertThat(distance).isBetween(40.0, 45.0);
    }

    @Test
    @DisplayName("Should return shop detail by ID or throw ResourceNotFoundException")
    void getShopById() {
        RepairShop shop = RepairShop.builder()
                .id("shop-db-1")
                .shopName("DB Repair Shop")
                .address("123 Street")
                .rating(4.8)
                .latitude(37.7749)
                .longitude(-122.4194)
                .build();

        when(repairShopRepository.findById("shop-db-1")).thenReturn(Optional.of(shop));

        ShopDetailResponse response = repairShopService.getShopById("shop-db-1");
        assertThat(response.success()).isTrue();
        assertThat(response.data().shopName()).isEqualTo("DB Repair Shop");

        when(repairShopRepository.findById("unknown")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> repairShopService.getShopById("unknown"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
