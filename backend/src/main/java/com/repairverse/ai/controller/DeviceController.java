package com.repairverse.ai.controller;

import com.repairverse.ai.dto.DeviceDto.*;
import com.repairverse.ai.dto.DevicePassportDto.DevicePassportResponse;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.DevicePassportService;
import com.repairverse.ai.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Device & Digital Health Passport REST Controller
 * Base path: /api/v1/devices
 *
 * Provides device lifecycle management, registration, updates,
 * and Digital Health Passport aggregation.
 */
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
@Slf4j
public class DeviceController {

    private final DeviceService deviceService;
    private final DevicePassportService devicePassportService;

    /**
     * GET /api/v1/devices
     * Retrieves all devices registered to the authenticated user.
     */
    @GetMapping
    public ResponseEntity<DeviceListResponse> getUserDevices(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = getUserId(userPrincipal);
        DeviceListResponse response = deviceService.getUserDevices(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/devices/{id}
     * Retrieves a single device by ID for the authenticated user.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getDeviceById(
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String userId = getUserId(userPrincipal);
        DeviceResponse response = deviceService.getDeviceById(id, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/devices
     * Registers a new device and initializes its Digital Health Passport.
     */
    @PostMapping
    public ResponseEntity<DeviceResponse> createDevice(
            @Valid @RequestBody CreateDeviceRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String userId = getUserId(userPrincipal);
        DeviceResponse response = deviceService.createDevice(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/v1/devices/{id}
     * Updates an existing device's details.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> updateDevice(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateDeviceRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String userId = getUserId(userPrincipal);
        DeviceResponse response = deviceService.updateDevice(id, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/devices/{id}
     * Removes a device and its associated health passport.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDevice(
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String userId = getUserId(userPrincipal);
        deviceService.deleteDevice(id, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Device deleted successfully"
        ));
    }

    /**
     * GET /api/v1/devices/{id}/passport
     * Retrieves the comprehensive Digital Health Passport for a device.
     */
    @GetMapping("/{id}/passport")
    public ResponseEntity<DevicePassportResponse> getDevicePassport(
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String userId = getUserId(userPrincipal);
        DevicePassportResponse response = devicePassportService.getDevicePassport(id, userId);
        return ResponseEntity.ok(response);
    }

    private String getUserId(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            // For unauthenticated or mock test contexts
            return "usr_demo";
        }
        return userPrincipal.getId();
    }
}
