package com.repairverse.ai.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.repairverse.ai.config.AppProperties;
import com.repairverse.ai.exception.InvalidFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    private AppProperties.Cloudinary cloudinaryProps;

    @BeforeEach
    void setUp() {
        cloudinaryProps = new AppProperties.Cloudinary();
        cloudinaryProps.setCloudName("test-cloud");
        cloudinaryProps.setApiKey("test-key");
        cloudinaryProps.setApiSecret("test-secret");
    }

    @Test
    @DisplayName("Should successfully upload a valid image and return secure URL")
    void testUploadSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "phone.jpg",
                "image/jpeg",
                "dummy image content".getBytes()
        );

        when(appProperties.getCloudinary()).thenReturn(cloudinaryProps);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/test-cloud/image/upload/sample.jpg"));

        String resultUrl = cloudinaryService.uploadImage(file);

        assertNotNull(resultUrl);
        assertEquals("https://res.cloudinary.com/test-cloud/image/upload/sample.jpg", resultUrl);
        verify(uploader, times(1)).upload(any(byte[].class), anyMap());
    }

    @Test
    @DisplayName("Should throw InvalidFileException when file is empty")
    void testEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("image", "empty.jpg", "image/jpeg", new byte[0]);

        assertThrows(InvalidFileException.class, () -> cloudinaryService.uploadImage(emptyFile));
    }

    @Test
    @DisplayName("Should throw InvalidFileException on unsupported file format")
    void testUnsupportedFormat() {
        MockMultipartFile textFile = new MockMultipartFile("image", "notes.txt", "text/plain", "hello".getBytes());

        assertThrows(InvalidFileException.class, () -> cloudinaryService.uploadImage(textFile));
    }

    @Test
    @DisplayName("Should return fallback URL gracefully if Cloudinary credentials are not configured")
    void testUnconfiguredFallback() {
        AppProperties.Cloudinary unconfiguredProps = new AppProperties.Cloudinary();
        unconfiguredProps.setCloudName("");
        unconfiguredProps.setApiKey("");
        unconfiguredProps.setApiSecret("");

        MockMultipartFile file = new MockMultipartFile("image", "phone.jpg", "image/jpeg", "content".getBytes());
        when(appProperties.getCloudinary()).thenReturn(unconfiguredProps);

        String resultUrl = cloudinaryService.uploadImage(file);

        assertNotNull(resultUrl);
        assertTrue(resultUrl.startsWith("https://"));
        verify(uploader, never()).upload(any(byte[].class), anyMap());
    }
}
