package com.repairverse.ai.service;

import com.repairverse.ai.dto.BookingDto.*;
import com.repairverse.ai.entity.Booking;
import com.repairverse.ai.entity.RepairShop;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.BookingRepository;
import com.repairverse.ai.repository.RepairShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RepairShopRepository repairShopRepository;
    private final RepairShopService repairShopService;
    private final NotificationService notificationService;

    @Transactional
    public BookingDetailResponse createBooking(CreateBookingRequest request, String userId) {
        String shopId = request.shopId();
        String bookingDate = request.bookingDate();
        String notes = request.notes();

        if (bookingDate == null || bookingDate.isBlank()) {
            throw new IllegalArgumentException("Booking date and time are required");
        }

        // Validate shop existence
        String shopName = "Repair Shop";
        Optional<RepairShop> shopOpt = repairShopRepository.findById(shopId);
        if (shopOpt.isPresent()) {
            shopName = shopOpt.get().getShopName();
        } else {
            // Check sample shops
            var sampleOpt = repairShopService.getSampleShops(null, null).stream()
                    .filter(s -> s.id().equalsIgnoreCase(shopId))
                    .findFirst();
            if (sampleOpt.isPresent()) {
                shopName = sampleOpt.get().shopName();
            } else {
                throw new ResourceNotFoundException("Repair shop not found with ID: " + shopId);
            }
        }

        // Check duplicate active booking conflict
        boolean exists = bookingRepository.existsByUserIdAndShopIdAndBookingDateAndBookingStatusNot(
                userId, shopId, bookingDate, "CANCELLED"
        );
        if (exists) {
            throw new IllegalStateException("A booking for this shop at the specified time already exists.");
        }

        Booking booking = Booking.builder()
                .id("book-" + UUID.randomUUID().toString().substring(0, 8))
                .userId(userId)
                .shopId(shopId)
                .bookingDate(bookingDate)
                .bookingStatus("SCHEDULED")
                .notes(notes)
                .createdAt(LocalDateTime.now())
                .build();

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created: '{}' for user '{}' at shop '{}'", saved.getId(), userId, shopId);

        // System event hook: dispatch notification to NotificationService
        notificationService.createNotification(
                userId,
                "shop",
                "Repair Booking Confirmed",
                "Your appointment at " + shopName + " on " + bookingDate + " has been confirmed.",
                "/dashboard/notifications",
                "View Booking",
                "green"
        );

        return new BookingDetailResponse(true, "Booking confirmed successfully", mapToDto(saved, shopName));
    }

    @Transactional(readOnly = true)
    public BookingListResponse getUserBookings(String userId) {
        List<BookingResponse> list = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(b -> mapToDto(b, getShopNameOrDefault(b.getShopId())))
                .toList();

        return new BookingListResponse(true, "User bookings retrieved successfully", list);
    }

    @Transactional
    public BookingDetailResponse cancelBooking(String bookingId, String userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUserId().equals(userId)) {
            log.warn("Unauthorized attempt by user '{}' to cancel booking '{}' owned by '{}'",
                    userId, bookingId, booking.getUserId());
            throw new AccessDeniedException("You are not authorized to cancel this booking");
        }

        if ("CANCELLED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        if ("COMPLETED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new IllegalArgumentException("Cannot cancel a completed booking");
        }

        booking.setBookingStatus("CANCELLED");
        Booking updated = bookingRepository.save(booking);
        String shopName = getShopNameOrDefault(updated.getShopId());

        log.info("Booking '{}' cancelled by user '{}'", bookingId, userId);

        // System event hook: notification
        notificationService.createNotification(
                userId,
                "shop",
                "Booking Cancelled",
                "Your booking at " + shopName + " scheduled for " + updated.getBookingDate() + " was cancelled.",
                "/dashboard/notifications",
                "View Notifications",
                "yellow"
        );

        return new BookingDetailResponse(true, "Booking cancelled successfully", mapToDto(updated, shopName));
    }

    private String getShopNameOrDefault(String shopId) {
        return repairShopRepository.findById(shopId)
                .map(RepairShop::getShopName)
                .orElse("Tech Repair Centre");
    }

    private BookingResponse mapToDto(Booking b, String shopName) {
        return new BookingResponse(
                b.getId(),
                b.getUserId(),
                b.getShopId(),
                shopName,
                b.getBookingDate(),
                b.getBookingStatus(),
                b.getBookingStatus(),
                b.getNotes(),
                b.getCreatedAt() != null ? b.getCreatedAt().toString() : LocalDateTime.now().toString()
        );
    }
}
