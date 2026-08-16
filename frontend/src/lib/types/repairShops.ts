import { ApiResponse } from "@/lib/types/auth";

/**
 * Matches RepairShops table in DATABASE_SCHEMA.md Module 6
 */
export type RepairShop = {
  id: string;
  shopName: string;
  name?: string;
  ownerName?: string;
  address: string;
  latitude?: number;
  longitude?: number;
  rating: number; // 0.0 – 5.0
  reviewCount?: number;
  phone?: string;
  email?: string;
  website?: string;
  hours?: string;
  services?: string[];
  serviceCategories?: string[];
  certifiedBrands?: string[];
  estimatedTurnaround?: string;
  avgPrice?: string;
  isVerified?: boolean;
  verified?: boolean;
  isOpen?: boolean;
  distanceKm?: number;
  ecoCertified?: boolean;
};

export type RepairShopSearchRequest = {
  latitude?: number;
  longitude?: number;
  radiusKm?: number;
  serviceCategory?: string;
};

export type RepairShopsListResponse = ApiResponse<RepairShop[]>;
export type RepairShopDetailResponse = ApiResponse<RepairShop>;
