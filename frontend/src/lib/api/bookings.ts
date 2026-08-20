import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import { isDemoSession } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";

export type CreateBookingRequest = {
  shopId: string;
  bookingDate: string;
  notes?: string;
};

export type BookingItem = {
  id: string;
  userId: string;
  shopId: string;
  shopName: string;
  bookingDate: string;
  bookingStatus: "SCHEDULED" | "CANCELLED" | "COMPLETED" | string;
  status: string;
  notes?: string;
  createdAt: string;
};

export type BookingListResponse = ApiResponse<BookingItem[]>;
export type BookingDetailResponse = ApiResponse<BookingItem>;

// Reference sample bookings for Demo Mode
export const SAMPLE_BOOKINGS: BookingItem[] = [
  {
    id: "book-sample-1",
    userId: "demo-user",
    shopId: "shop-001",
    shopName: "TechCare Express Repair",
    bookingDate: "2026-08-25 10:30 AM",
    bookingStatus: "SCHEDULED",
    status: "SCHEDULED",
    notes: "iPhone 13 screen replacement & battery check",
    createdAt: new Date(Date.now() - 3600 * 1000).toISOString(),
  },
];

/**
 * Create a new repair shop booking.
 * POST /api/v1/bookings
 */
export async function createBooking(
  data: CreateBookingRequest,
  token?: string,
  signal?: AbortSignal
): Promise<BookingDetailResponse> {
  if (isDemoSession(token)) {
    const newBooking: BookingItem = {
      id: `book-demo-${Date.now()}`,
      userId: "demo-user",
      shopId: data.shopId,
      shopName: "Selected Repair Shop (Demo)",
      bookingDate: data.bookingDate,
      bookingStatus: "SCHEDULED",
      status: "SCHEDULED",
      notes: data.notes,
      createdAt: new Date().toISOString(),
    };
    SAMPLE_BOOKINGS.unshift(newBooking);
    return {
      success: true,
      message: "Appointment booked successfully (Demo Mode)",
      data: newBooking,
    };
  }

  const result = await apiClient<BookingItem>("/bookings", {
    method: "POST",
    token,
    body: data,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, message: result.message || "Booking created successfully", data: result.data };
  }

  return {
    success: false,
    message: result.message || `Failed to create booking. API at ${API_BASE_URL}/bookings offline.`,
  };
}

/**
 * Fetch all bookings for the authenticated user.
 * GET /api/v1/bookings
 */
export async function fetchUserBookings(
  token?: string,
  signal?: AbortSignal
): Promise<BookingListResponse> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "User bookings retrieved (Demo Mode)",
      data: SAMPLE_BOOKINGS,
    };
  }

  const result = await apiClient<BookingItem[]>("/bookings", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data };
  }

  return {
    success: true,
    message: `Backend booking service at ${API_BASE_URL}/bookings is offline. Displaying sample bookings.`,
    data: SAMPLE_BOOKINGS,
  };
}

/**
 * Cancel a booking by ID.
 * DELETE /api/v1/bookings/{id}
 */
export async function cancelBooking(
  bookingId: string,
  token?: string,
  signal?: AbortSignal
): Promise<BookingDetailResponse> {
  if (isDemoSession(token)) {
    const existing = SAMPLE_BOOKINGS.find((b) => b.id === bookingId);
    if (existing) {
      existing.bookingStatus = "CANCELLED";
      existing.status = "CANCELLED";
    }
    return {
      success: true,
      message: "Booking cancelled successfully (Demo Mode)",
      data: existing,
    };
  }

  const result = await apiClient<BookingItem>(`/bookings/${bookingId}`, {
    method: "DELETE",
    token,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, message: result.message || "Booking cancelled", data: result.data };
  }

  return {
    success: false,
    message: result.message || "Failed to cancel booking",
  };
}
