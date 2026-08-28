package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairPlanningDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.RepairJourneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Smart Repair Journey REST Controller
 * Base path: /api/v1/repair-journey
 */
@RestController
@RequestMapping("/repair-journey")
@RequiredArgsConstructor
@Slf4j
public class RepairJourneyController {

    private final RepairJourneyService repairJourneyService;

    /**
     * GET /api/v1/repair-journey/device/{deviceId}
     * Retrieve the 9-stage unified repair journey status for a device.
     */
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Map<String, Object>> getRepairJourney(
            @PathVariable("deviceId") String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Repair journey requested for device='{}' by user='{}'", deviceId, userId);

        RepairJourneyResponse journey = repairJourneyService.getRepairJourney(deviceId, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", journey,
                "message", "Repair journey timeline retrieved successfully"
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) return "usr-1";
        return principal.getId();
    }
}
