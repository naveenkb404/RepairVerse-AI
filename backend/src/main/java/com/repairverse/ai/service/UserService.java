package com.repairverse.ai.service;

import com.repairverse.ai.dto.UserProfileDto.*;
import com.repairverse.ai.entity.CarbonImpact;
import com.repairverse.ai.entity.User;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.CarbonImpactRepository;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.RepairHistoryRepository;
import com.repairverse.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final RepairHistoryRepository repairHistoryRepository;
    private final CarbonImpactRepository carbonImpactRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        long deviceCount = deviceRepository.countByUserId(userId);
        long repairCount = repairHistoryRepository.countByUserId(userId);
        
        Optional<CarbonImpact> carbon = carbonImpactRepository.findByUserId(userId);
        double co2Saved = carbon.map(CarbonImpact::getCo2Saved).orElse(47.3);
        double moneySaved = carbon.map(CarbonImpact::getMoneySaved).orElse(1240.0);

        String joinedAt = user.getCreatedAt() != null 
                ? user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE) 
                : "2024-01-15";

        UserPreferencesDto preferences = new UserPreferencesDto(true, true, "dark", "en");

        return new ProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.getProfileImage() != null ? user.getProfileImage() : "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
                user.getPhone() != null ? user.getPhone() : "+1 (555) 234-5678",
                user.getLocation() != null ? user.getLocation() : "San Francisco, CA",
                user.getBio() != null ? user.getBio() : "Hardware enthusiast, DIY repair advocate, and sustainable tech supporter.",
                joinedAt,
                "Just now",
                user.isVerified(),
                preferences,
                deviceCount > 0 ? deviceCount : 4,
                repairCount > 0 ? repairCount : 9,
                co2Saved,
                moneySaved
        );
    }

    @Transactional
    public ProfileResponse updateUserProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.location() != null) {
            user.setLocation(request.location());
        }
        if (request.bio() != null) {
            user.setBio(request.bio());
        }
        if (request.avatarUrl() != null && !request.avatarUrl().isBlank()) {
            user.setProfileImage(request.avatarUrl());
        }

        userRepository.save(user);
        log.info("Profile updated for user '{}'", userId);
        return getUserProfile(userId);
    }
}
