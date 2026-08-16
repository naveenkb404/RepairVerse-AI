package com.repairverse.ai.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CloudinaryConfig {

    private final AppProperties appProperties;

    @Bean
    public Cloudinary cloudinary() {
        String cloudName = appProperties.getCloudinary().getCloudName();
        String apiKey = appProperties.getCloudinary().getApiKey();
        String apiSecret = appProperties.getCloudinary().getApiSecret();

        if (StringUtils.hasText(cloudName) && StringUtils.hasText(apiKey) && StringUtils.hasText(apiSecret)) {
            return new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret,
                    "secure", true
            ));
        }

        log.warn("Cloudinary credentials are not configured. Image uploads will require local/mock mode.");
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "demo-cloud",
                "api_key", "demo-key",
                "api_secret", "demo-secret",
                "secure", true
        ));
    }
}
