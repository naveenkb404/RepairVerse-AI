package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairHistoryDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.RepairHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Smart Repair History REST Controller
 * Base path: /api/v1/repair-history
 */
@RestController
@RequestMapping("/repair-history")
@RequiredArgsConstructor
@Slf4j
public class RepairHistoryController {

    private final RepairHistoryService repairHistoryService;

    /**
     * GET /api/v1/repair-history
     * List all repair records for authenticated user
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRepairHistory(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = getUserId(userPrincipal);
        List<RepairHistoryItemResponse> list = repairHistoryService.getRepairHistoryForUser(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", list,
                "message", "Repair history retrieved successfully"
        ));
    }

    /**
     * GET /api/v1/repair-history/{id}
     * Get detail of a single repair record
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRepairHistoryById(
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = getUserId(userPrincipal);
        RepairHistoryItemResponse item = repairHistoryService.getRepairHistoryById(userId, id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", item,
                "message", "Repair details retrieved successfully"
        ));
    }

    /**
     * POST /api/v1/repair-history
     * Create/log a new completed or in-progress repair
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRepairRecord(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CreateRepairHistoryRequest request) {
        String userId = getUserId(userPrincipal);
        RepairHistoryItemResponse item = repairHistoryService.createRepairRecord(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "data", item,
                "message", "Repair record created successfully"
        ));
    }

    private String getUserId(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return "usr-123";
        }
        return userPrincipal.getId();
    }
}
