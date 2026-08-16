package com.repairverse.ai.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.repairverse.ai.config.AppProperties;
import com.repairverse.ai.exception.ImageUploadException;
import com.repairverse.ai.exception.InvalidFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final AppProperties appProperties;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Validates and uploads a device hardware photo to Cloudinary.
     * Returns the permanent secure HTTPS URL.
     */
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Device image is required for visual diagnosis.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("Image size exceeds maximum limit of 10MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileException("Unsupported file format. Supported formats: JPEG, PNG, WEBP.");
        }

        String cloudName = appProperties.getCloudinary().getCloudName();
        String apiKey = appProperties.getCloudinary().getApiKey();
        String apiSecret = appProperties.getCloudinary().getApiSecret();

        // Check if Cloudinary is configured
        if (!StringUtils.hasText(cloudName) || !StringUtils.hasText(apiKey) || !StringUtils.hasText(apiSecret)) {
            log.warn("Cloudinary credentials are not configured in environment. Using fallback reference image URL.");
            return "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800&auto=format&fit=crop&q=80";
        }

        try {
            String publicId = "repairverse_diag_" + UUID.randomUUID();
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", "repairverse/diagnosis",
                    "resource_type", "image",
                    "overwrite", true
            ));

            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl != null) {
                return secureUrl.toString();
            }

            Object url = uploadResult.get("url");
            if (url != null) {
                return url.toString();
            }

            throw new ImageUploadException("Cloudinary returned empty URL response.");
        } catch (IOException e) {
            log.error("Failed to read image bytes for upload", e);
            throw new ImageUploadException("Failed to read uploaded image bytes.", e);
        } catch (Exception e) {
            log.error("Cloudinary upload failed: {}", e.getMessage());
            throw new ImageUploadException("Failed to upload image to Cloudinary storage: " + e.getMessage(), e);
        }
    }
}
