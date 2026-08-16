package com.repairverse.ai.controller;

import com.repairverse.ai.dto.DiagnosisResponseDto.DiagnosisReportDto;
import com.repairverse.ai.dto.DiagnosisResponseDto.DiagnosisResponse;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * AI Diagnosis REST Controller
 * Base path: /api/v1/diagnosis
 *
 * Handles visual device inspection, component diagnosis, and fault prediction
 * via Google Gemini 1.5 Vision and Cloudinary image persistence.
 */
@RestController
@RequestMapping("/diagnosis")
@RequiredArgsConstructor
@Slf4j
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    /**
     * POST /api/v1/diagnosis
     * Consumes: multipart/form-data
     *
     * Uploads device photo to Cloudinary, executes Gemini AI Vision analysis,
     * persists diagnosis report, and returns structured diagnosis.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiagnosisResponse> diagnose(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "deviceId", required = false) String deviceId,
            @RequestParam(value = "deviceCategory", required = false) String deviceCategory,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam("symptoms") String symptoms,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String userId = userPrincipal != null ? userPrincipal.getId() : null;

        DiagnosisResponse response = diagnosisService.diagnoseDevice(
                image,
                deviceId,
                deviceCategory,
                brand,
                model,
                symptoms,
                userId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/diagnosis/{id}
     * Retrieves a stored diagnosis report by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisReportDto> getReport(@PathVariable("id") String id) {
        DiagnosisReportDto report = diagnosisService.getDiagnosisReport(id);
        return ResponseEntity.ok(report);
    }
}
