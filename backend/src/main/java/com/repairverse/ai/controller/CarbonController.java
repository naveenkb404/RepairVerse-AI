package com.repairverse.ai.controller;

import com.repairverse.ai.dto.CarbonDto.CarbonDashboardResponse;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.CarbonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Carbon Impact REST Controller
 * Base path: /api/v1/carbon
 */
@RestController
@RequestMapping("/carbon")
@RequiredArgsConstructor
@Slf4j
public class CarbonController {

    private final CarbonService carbonService;

    /**
     * GET /api/v1/carbon
     * Authenticated endpoint returning user's carbon impact dashboard data.
     */
    @GetMapping
    public ResponseEntity<CarbonDashboardResponse> getCarbonDashboard(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = getUserId(userPrincipal);
        CarbonDashboardResponse response = carbonService.getCarbonDashboard(userId);
        return ResponseEntity.ok(response);
    }

    private String getUserId(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return "usr-123";
        }
        return userPrincipal.getId();
    }
}


