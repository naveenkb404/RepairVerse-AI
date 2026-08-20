package com.repairverse.ai.repository;

import com.repairverse.ai.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<Booking> findByIdAndUserId(String id, String userId);
    boolean existsByUserIdAndShopIdAndBookingDateAndBookingStatusNot(String userId, String shopId, String bookingDate, String bookingStatus);
}
