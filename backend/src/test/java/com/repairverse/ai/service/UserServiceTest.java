package com.repairverse.ai.service;

import com.repairverse.ai.dto.UserProfileDto.*;
import com.repairverse.ai.entity.Role;
import com.repairverse.ai.entity.User;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.CarbonImpactRepository;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.RepairHistoryRepository;
import com.repairverse.ai.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private RepairHistoryRepository repairHistoryRepository;

    @Mock
    private CarbonImpactRepository carbonImpactRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should return user profile successfully")
    void getUserProfile_Success() {
        User user = User.builder()
                .id("usr-1")
                .fullName("John Doe")
                .email("john@example.com")
                .role(Role.USER)
                .verified(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById("usr-1")).thenReturn(Optional.of(user));
        when(deviceRepository.countByUserId("usr-1")).thenReturn(3L);
        when(repairHistoryRepository.countByUserId("usr-1")).thenReturn(5L);
        when(carbonImpactRepository.findByUserId("usr-1")).thenReturn(Optional.empty());

        ProfileResponse response = userService.getUserProfile("usr-1");

        assertThat(response.id()).isEqualTo("usr-1");
        assertThat(response.fullName()).isEqualTo("John Doe");
        assertThat(response.totalDevices()).isEqualTo(3);
        assertThat(response.totalRepairs()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user does not exist")
    void getUserProfile_NotFound() {
        when(userRepository.findById("usr-99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile("usr-99"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should update user profile successfully")
    void updateUserProfile_Success() {
        User user = User.builder()
                .id("usr-1")
                .fullName("Old Name")
                .email("john@example.com")
                .role(Role.USER)
                .verified(true)
                .build();

        when(userRepository.findById("usr-1")).thenReturn(Optional.of(user));
        when(deviceRepository.countByUserId("usr-1")).thenReturn(1L);
        when(repairHistoryRepository.countByUserId("usr-1")).thenReturn(1L);
        when(carbonImpactRepository.findByUserId("usr-1")).thenReturn(Optional.empty());

        UpdateProfileRequest request = new UpdateProfileRequest("New Name", "+1234567890", "NYC", "Bio text", "avatar.jpg");
        ProfileResponse updated = userService.updateUserProfile("usr-1", request);

        assertThat(user.getFullName()).isEqualTo("New Name");
        assertThat(user.getPhone()).isEqualTo("+1234567890");
        assertThat(user.getLocation()).isEqualTo("NYC");
        assertThat(user.getBio()).isEqualTo("Bio text");
        verify(userRepository, times(1)).save(user);
    }
}
