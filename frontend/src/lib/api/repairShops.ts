import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import type {
  RepairShop,
  RepairShopDetailResponse,
  RepairShopSearchRequest,
  RepairShopsListResponse,
} from "@/lib/types/repairShops";

// ─── Reference Sample Repair Shops for Demo/Offline Mode ────────────────────

export const SAMPLE_SHOPS: RepairShop[] = [
  {
    id: "shop-001",
    shopName: "TechCare Express Repair",
    name: "TechCare Express Repair",
    address: "742 Market Street, Suite 300, San Francisco, CA 94103",
    rating: 4.9,
    reviewCount: 328,
    distanceKm: 0.8,
    estimatedTurnaround: "Same-Day (2-4 hrs)",
    avgPrice: "$45 – $120",
    services: ["Smartphone", "Tablet", "Laptop", "Micro-soldering"],
    serviceCategories: ["Smartphone Repair", "Laptop Repair", "Tablet Repair"],
    certifiedBrands: ["Apple", "Samsung", "Google Pixel"],
    isOpen: true,
    phone: "+1 (415) 555-0192",
    email: "sf-downtown@techcare.io",
    hours: "Mon–Sat: 9:00 AM – 7:00 PM",
    verified: true,
    ecoCertified: true,
  },
  {
    id: "shop-002",
    shopName: "GreenCircuit Electronics Lab",
    name: "GreenCircuit Electronics Lab",
    address: "1280 Folsom St, San Francisco, CA 94103",
    rating: 4.8,
    reviewCount: 194,
    distanceKm: 1.4,
    estimatedTurnaround: "1-2 Business Days",
    avgPrice: "$60 – $180",
    services: ["Laptop", "Gaming Console", "Audio Equipment", "Logic Board Repair"],
    serviceCategories: ["Laptop Repair", "Gaming Console Repair", "Audio/Headphone Repair"],
    certifiedBrands: ["Apple", "Sony PlayStation", "Dell", "Lenovo"],
    isOpen: true,
    phone: "+1 (415) 555-0348",
    email: "contact@greencircuitlab.com",
    hours: "Tue–Sun: 10:00 AM – 6:00 PM",
    verified: true,
    ecoCertified: true,
  },
  {
    id: "shop-003",
    shopName: "QuickFix Mobile Hub",
    name: "QuickFix Mobile Hub",
    address: "550 Mission St, San Francisco, CA 94105",
    rating: 4.7,
    reviewCount: 412,
    distanceKm: 2.1,
    estimatedTurnaround: "30-60 Mins",
    avgPrice: "$35 – $95",
    services: ["Smartphone", "Smartwatch", "Battery Replacement", "Screen Repair"],
    serviceCategories: ["Smartphone Repair", "Smartwatch Repair"],
    certifiedBrands: ["Apple", "Samsung", "Xiaomi", "OnePlus"],
    isOpen: true,
    phone: "+1 (415) 555-0781",
    email: "support@quickfixhub.net",
    hours: "Mon–Fri: 8:30 AM – 8:00 PM",
    verified: true,
    ecoCertified: false,
  },
  {
    id: "shop-004",
    shopName: "Silicon Valley Tech Clinic",
    name: "Silicon Valley Tech Clinic",
    address: "2100 University Ave, Palo Alto, CA 94301",
    rating: 4.9,
    reviewCount: 520,
    distanceKm: 5.3,
    estimatedTurnaround: "Same-Day",
    avgPrice: "$70 – $220",
    services: ["Laptop", "Smartphone", "Tablet", "Data Recovery", "Component Soldering"],
    serviceCategories: ["Laptop Repair", "PC Repair", "Tablet Repair"],
    certifiedBrands: ["Apple", "Microsoft Surface", "HP", "Asus"],
    isOpen: false,
    phone: "+1 (650) 555-0914",
    email: "service@svtechclinic.com",
    hours: "Mon–Sat: 9:00 AM – 6:00 PM",
    verified: true,
    ecoCertified: true,
  },
];


/**
 * Repair Shops API Service Layer
 * Documented endpoints (docs/API_SPEC.md):
 *   GET /api/v1/shops            — list/search shops
 *   GET /api/v1/shops/{id}       — single shop detail
 */

export async function fetchNearbyShops(
  params: RepairShopSearchRequest,
  token?: string,
  signal?: AbortSignal
): Promise<RepairShopsListResponse> {
  const result = await apiClient<RepairShop[]>("/shops", {
    method: "GET",
    params: {
      latitude: params.latitude,
      longitude: params.longitude,
      radius: params.radiusKm,
      serviceCategory: params.serviceCategory,
    },
    token,
    signal,
  });

  if (result.success && result.data && Array.isArray(result.data) && result.data.length > 0) {
    return { success: true, data: result.data };
  }

  // Fallback to reference sample shops
  return {
    success: true,
    message: `Backend shop service at ${API_BASE_URL}/shops is offline. Displaying verified sample repair shops.`,
    data: SAMPLE_SHOPS,
  };
}

export async function fetchShopDetail(
  shopId: string,
  token?: string,
  signal?: AbortSignal
): Promise<RepairShopDetailResponse> {
  const result = await apiClient<RepairShop>(`/shops/${shopId}`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data };
  }

  const sample = SAMPLE_SHOPS.find((s) => s.id === shopId) || SAMPLE_SHOPS[0];
  return {
    success: true,
    message: `Backend shop service at ${API_BASE_URL}/shops/${shopId} is offline. Displaying reference shop details.`,
    data: sample,
  };
}
