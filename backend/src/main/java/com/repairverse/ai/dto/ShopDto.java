package com.repairverse.ai.dto;

import java.util.List;

public class ShopDto {

    public record RepairShopResponse(
            String id,
            String shopName,
            String name,
            String ownerName,
            String address,
            Double latitude,
            Double longitude,
            Double rating,
            Integer reviewCount,
            String phone,
            String email,
            String hours,
            List<String> services,
            List<String> serviceCategories,
            List<String> certifiedBrands,
            String estimatedTurnaround,
            String avgPrice,
            Boolean isVerified,
            Boolean verified,
            Boolean isOpen,
            Double distanceKm,
            Boolean ecoCertified,
            Boolean isDemo
    ) {}

    public record ShopListResponse(
            boolean success,
            String message,
            List<RepairShopResponse> data
    ) {}

    public record ShopDetailResponse(
            boolean success,
            String message,
            RepairShopResponse data
    ) {}
}
