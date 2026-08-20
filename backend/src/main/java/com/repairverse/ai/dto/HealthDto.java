package com.repairverse.ai.dto;

import java.util.Map;

public class HealthDto {

    public record SystemHealthData(
            String status,
            String timestamp,
            String system,
            String version,
            Map<String, String> services,
            String activeProfiles
    ) {}

    public record SystemHealthResponse(
            boolean success,
            String message,
            SystemHealthData data
    ) {}
}
