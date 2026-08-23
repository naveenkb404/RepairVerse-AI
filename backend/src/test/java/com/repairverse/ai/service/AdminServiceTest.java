package com.repairverse.ai.service;

import com.repairverse.ai.dto.AdminDto.*;
import com.repairverse.ai.entity.Role;
import com.repairverse.ai.entity.User;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;

    @Mock
    private RepairHistoryRepository repairHistoryRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    @DisplayName("Should return all users summary for admin")
    void getAllUsers_Success() {
        User user = User.builder().id("usr-1").fullName("Admin User").email("admin@repairverse.ai").role(Role.ADMIN).verified(true).build();
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(deviceRepository.countByUserId("usr-1")).thenReturn(2L);

        List<AdminUserSummary> list = adminService.getAllUsers();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).email()).isEqualTo("admin@repairverse.ai");
    }

    @Test
    @DisplayName("Should return platform analytics")
    void getAnalytics_Success() {
        when(userRepository.count()).thenReturn(100L);
        when(deviceRepository.count()).thenReturn(250L);
        when(diagnosisReportRepository.count()).thenReturn(400L);
        when(repairHistoryRepository.count()).thenReturn(300L);
        when(bookingRepository.count()).thenReturn(150L);

        AdminAnalyticsResponse analytics = adminService.getAnalytics();

        assertThat(analytics.totalUsers()).isEqualTo(100);
        assertThat(analytics.totalDevices()).isEqualTo(250);
        assertThat(analytics.totalDiagnoses()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should delete user or throw ResourceNotFoundException")
    void deleteUser_Success() {
        User user = User.builder().id("usr-to-del").build();
        when(userRepository.findById("usr-to-del")).thenReturn(Optional.of(user));

        adminService.deleteUser("usr-to-del");

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if user to delete not found")
    void deleteUser_NotFound() {
        when(userRepository.findById("usr-none")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.deleteUser("usr-none"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
