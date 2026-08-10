import {
  RepairShopSearchRequest,
  RepairShopsListResponse,
  RepairShopDetailResponse,
} from "@/lib/types/repairShops";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

/**
 * Repair Shops API Service Layer
 * Documented endpoints (docs/API_SPEC.md):
 *   GET /api/v1/shops            — list/search shops
 *   GET /api/v1/shops/{id}       — single shop detail
 */
export async function fetchNearbyShops(
  params: RepairShopSearchRequest,
  token?: string
): Promise<RepairShopsListResponse> {
  try {
    const query = new URLSearchParams();
    if (params.latitude !== undefined)
      query.set("latitude", String(params.latitude));
    if (params.longitude !== undefined)
      query.set("longitude", String(params.longitude));
    if (params.radiusKm !== undefined)
      query.set("radius", String(params.radiusKm));
    if (params.serviceCategory)
      query.set("serviceCategory", params.serviceCategory);

    const headers: HeadersInit = { "Content-Type": "application/json" };
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const response = await fetch(
      `${API_BASE_URL}/shops?${query.toString()}`,
      { method: "GET", headers, cache: "no-store" }
    );

    if (!response.ok) {
      const err = await response.json().catch(() => null);
      return {
        success: false,
        message: err?.message || `Shop search failed (HTTP ${response.status})`,
      };
    }

    const data = await response.json();
    return { success: true, data: data.data || data };
  } catch {
    return {
      success: false,
      message:
        "Repair shops backend service is currently offline. " +
        "Start the Spring Boot API at " +
        API_BASE_URL,
    };
  }
}

export async function fetchShopDetail(
  shopId: string,
  token?: string
): Promise<RepairShopDetailResponse> {
  try {
    const headers: HeadersInit = { "Content-Type": "application/json" };
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const response = await fetch(`${API_BASE_URL}/shops/${shopId}`, {
      method: "GET",
      headers,
      cache: "no-store",
    });

    if (!response.ok) {
      const err = await response.json().catch(() => null);
      return {
        success: false,
        message:
          err?.message || `Shop detail request failed (HTTP ${response.status})`,
      };
    }

    const data = await response.json();
    return { success: true, data: data.data || data };
  } catch {
    return {
      success: false,
      message: "Repair shops backend service is currently offline.",
    };
  }
}
