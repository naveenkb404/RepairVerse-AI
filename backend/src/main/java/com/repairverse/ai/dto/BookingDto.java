package com.repairverse.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class BookingDto {

    public record CreateBookingRequest(
            @NotBlank(message = "Shop ID is required")
            String shopId,

            @NotBlank(message = "Booking date/time is required")
            String bookingDate,

            @Size(max = 500, message = "Notes cannot exceed 500 characters")
            String notes
    ) {}

    public record BookingResponse(
            String id,
            String userId,
            String shopId,
            String shopName,
            String bookingDate,
            String bookingStatus,
            String status,
            String notes,
            String createdAt
    ) {}

    public record BookingListResponse(
            boolean success,
            String message,
            List<BookingResponse> data
    ) {}

    public record BookingDetailResponse(
            boolean success,
            String message,
            BookingResponse data
    ) {}
}
