package com.repairverse.ai.controller;

import com.repairverse.ai.dto.UserProfileDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * User Profile & Preferences REST Controller
 * Base path: /api/v1/users
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * GET /api/v1/users/profile
     * Fetch authenticated user's profile
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = getUserId(userPrincipal);
        ProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User profile retrieved successfully",
                "data", profile
        ));
    }

    /**
     * PUT /api/v1/users/profile
     * Update authenticated user's profile
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UpdateProfileRequest request) {
        String userId = getUserId(userPrincipal);
        ProfileResponse profile = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile updated successfully",
                "data", profile
        ));
    }

    private String getUserId(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return "usr-123";
        }
        return userPrincipal.getId();
    }
}
