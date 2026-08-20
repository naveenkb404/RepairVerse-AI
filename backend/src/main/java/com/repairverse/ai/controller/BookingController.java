package com.repairverse.ai.controller;

import com.repairverse.ai.dto.BookingDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Booking Pipeline REST Controller
 * Base path: /api/v1/bookings
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    /**
     * POST /api/v1/bookings
     * Authenticated endpoint to create a new repair shop booking.
     */
    @PostMapping
    public ResponseEntity<BookingDetailResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String userId = getUserId(userPrincipal);
        BookingDetailResponse response = bookingService.createBooking(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/bookings
     * Authenticated endpoint to list all bookings belonging to the user.
     */
    @GetMapping
    public ResponseEntity<BookingListResponse> getUserBookings(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = getUserId(userPrincipal);
        BookingListResponse response = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/bookings/{id}
     * Authenticated endpoint to cancel/delete a booking owned by the user.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BookingDetailResponse> cancelBooking(
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String userId = getUserId(userPrincipal);
        BookingDetailResponse response = bookingService.cancelBooking(id, userId);
        return ResponseEntity.ok(response);
    }

    private String getUserId(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return "usr-123";
        }
        return userPrincipal.getId();
    }
}


