package com.repairverse.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private final Cors cors = new Cors();
    private final Jwt jwt = new Jwt();
    private final Gemini gemini = new Gemini();
    private final Cloudinary cloudinary = new Cloudinary();

    @Getter
    @Setter
    public static class Cors {
        private String allowedOrigins = "http://localhost:3000";
    }

    @Getter
    @Setter
    public static class Jwt {
        // Secret MUST be provided via JWT_SECRET environment variable — no hardcoded default
        private String secret;
        private long expirationMs = 86400000; // 24 hours default
    }

    @Getter
    @Setter
    public static class Gemini {
        private String apiKey = "";
        private String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    }

    @Getter
    @Setter
    public static class Cloudinary {
        private String cloudName = "";
        private String apiKey = "";
        private String apiSecret = "";
    }
}
