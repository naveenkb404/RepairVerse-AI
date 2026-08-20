package com.repairverse.ai.service;

import com.repairverse.ai.config.AppProperties;
import com.repairverse.ai.dto.HealthDto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemHealthService {

    private final DataSource dataSource;
    private final AppProperties appProperties;
    private final Environment environment;

    public SystemHealthResponse getSystemHealth() {
        Map<String, String> services = new HashMap<>();

        // 1. Check Database Health
        boolean dbOk = checkDatabaseHealth();
        services.put("database", dbOk ? "UP" : "DOWN");
        services.put("flyway", dbOk ? "UP" : "DOWN");

        // 2. Check Gemini AI Config
        boolean geminiConfigured = appProperties.getGemini() != null &&
                appProperties.getGemini().getApiKey() != null &&
                !appProperties.getGemini().getApiKey().isBlank();
        services.put("geminiAi", geminiConfigured ? "CONFIGURED" : "FALLBACK_HEURISTIC");

        // 3. Check Cloudinary Config
        boolean cloudinaryConfigured = appProperties.getCloudinary() != null &&
                appProperties.getCloudinary().getCloudName() != null &&
                !appProperties.getCloudinary().getCloudName().isBlank();
        services.put("cloudinary", cloudinaryConfigured ? "CONFIGURED" : "LOCAL_MOCK");

        String overallStatus = dbOk ? "UP" : "DEGRADED";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String activeProfiles = String.join(",", environment.getActiveProfiles());
        if (activeProfiles.isBlank()) activeProfiles = "default";

        SystemHealthData data = new SystemHealthData(
                overallStatus,
                timestamp,
                "RepairVerse AI Platform Service",
                "1.0.0",
                services,
                activeProfiles
        );

        log.info("System health check performed. Overall status: {}", overallStatus);
        return new SystemHealthResponse(true, "System health report generated", data);
    }

    private boolean checkDatabaseHealth() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(2);
        } catch (Exception e) {
            log.warn("Database health check failed: {}", e.getMessage());
            return false;
        }
    }
}
