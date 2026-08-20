package com.repairverse.ai.controller;

import com.repairverse.ai.dto.HealthDto.SystemHealthResponse;
import com.repairverse.ai.service.SystemHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * System Health & Diagnostics REST Controller
 * Base path: /api/v1/health
 */
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    private final SystemHealthService systemHealthService;

    /**
     * GET /api/v1/health
     * Public endpoint returning system health diagnostic report.
     */
    @GetMapping
    public ResponseEntity<SystemHealthResponse> getHealth() {
        SystemHealthResponse response = systemHealthService.getSystemHealth();
        return ResponseEntity.ok(response);
    }
}
