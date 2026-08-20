package com.repairverse.ai.service;

import com.repairverse.ai.dto.BookingDto.*;
import com.repairverse.ai.entity.Booking;
import com.repairverse.ai.entity.RepairShop;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.BookingRepository;
import com.repairverse.ai.repository.RepairShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RepairShopRepository repairShopRepository;

    @Mock
    private RepairShopService repairShopService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingService bookingService;

    private String userId;
    private String shopId;

    @BeforeEach
    void setUp() {
        userId = "usr-owner";
        shopId = "shop-001";
    }

    @Test
    @DisplayName("Should create booking successfully and dispatch notification event hook")
    void createBooking_Success() {
        CreateBookingRequest request = new CreateBookingRequest(shopId, "2026-09-01 10:00 AM", "Screen replacement");

        RepairShop shop = RepairShop.builder().id(shopId).shopName("TechCare Express").build();
        when(repairShopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(bookingRepository.existsByUserIdAndShopIdAndBookingDateAndBookingStatusNot(eq(userId), eq(shopId), anyString(), eq("CANCELLED"))).thenReturn(false);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setCreatedAt(LocalDateTime.now());
            return b;
        });

        BookingDetailResponse response = bookingService.createBooking(request, userId);

        assertThat(response.success()).isTrue();
        assertThat(response.data().shopName()).isEqualTo("TechCare Express");
        assertThat(response.data().bookingStatus()).isEqualTo("SCHEDULED");

        // Verify notification hook was called
        verify(notificationService, times(1)).createNotification(
                eq(userId), eq("shop"), anyString(), anyString(), anyString(), anyString(), anyString()
        );
    }

    @Test
    @DisplayName("Should throw IllegalStateException (409 Conflict) when duplicate booking exists")
    void createBooking_DuplicateConflict() {
        CreateBookingRequest request = new CreateBookingRequest(shopId, "2026-09-01 10:00 AM", "Screen replacement");

        when(repairShopRepository.findById(shopId)).thenReturn(Optional.of(RepairShop.builder().id(shopId).shopName("Shop").build()));
        when(bookingRepository.existsByUserIdAndShopIdAndBookingDateAndBookingStatusNot(eq(userId), eq(shopId), anyString(), eq("CANCELLED"))).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(request, userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("A booking for this shop at the specified time already exists.");
    }

    @Test
    @DisplayName("Should throw AccessDeniedException (403 Forbidden) when user tries to cancel another user's booking")
    void cancelBooking_CrossUserForbidden() {
        Booking booking = Booking.builder()
                .id("book-99")
                .userId("other-user")
                .shopId(shopId)
                .bookingStatus("SCHEDULED")
                .build();

        when(bookingRepository.findById("book-99")).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking("book-99", userId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You are not authorized to cancel this booking");
    }

    @Test
    @DisplayName("Should cancel booking successfully when requested by owner")
    void cancelBooking_Success() {
        Booking booking = Booking.builder()
                .id("book-1")
                .userId(userId)
                .shopId(shopId)
                .bookingDate("2026-09-01 10:00 AM")
                .bookingStatus("SCHEDULED")
                .build();

        when(bookingRepository.findById("book-1")).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingDetailResponse response = bookingService.cancelBooking("book-1", userId);

        assertThat(response.success()).isTrue();
        assertThat(response.data().bookingStatus()).isEqualTo("CANCELLED");
        verify(notificationService, times(1)).createNotification(
                eq(userId), eq("shop"), eq("Booking Cancelled"), anyString(), anyString(), anyString(), anyString()
        );
    }
}
