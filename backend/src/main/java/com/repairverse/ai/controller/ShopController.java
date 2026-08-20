package com.repairverse.ai.controller;

import com.repairverse.ai.dto.ShopDto.*;
import com.repairverse.ai.service.RepairShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Certified Repair Shops REST Controller
 * Base path: /api/v1/shops
 */
@RestController
@RequestMapping("/shops")
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private final RepairShopService shopService;

    /**
     * GET /api/v1/shops
     * Public endpoint to list/search repair shops with Haversine distance, category, and rating filters.
     */
    @GetMapping
    public ResponseEntity<ShopListResponse> getShops(
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "radius", required = false) Double radius,
            @RequestParam(value = "radiusKm", required = false) Double radiusKm,
            @RequestParam(value = "serviceCategory", required = false) String serviceCategory,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "ecoCertified", required = false) Boolean ecoCertified,
            @RequestParam(value = "sortBy", required = false) String sortBy
    ) {
        Double effectiveRadius = radiusKm != null ? radiusKm : radius;
        ShopListResponse response = shopService.getShops(
                latitude, longitude, effectiveRadius, serviceCategory, search, minRating, ecoCertified, sortBy
        );
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/shops/{id}
     * Public endpoint to retrieve single shop details by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShopDetailResponse> getShopById(@PathVariable("id") String id) {
        ShopDetailResponse response = shopService.getShopById(id);
        return ResponseEntity.ok(response);
    }
}
