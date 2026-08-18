package com.repairverse.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.DeviceDto.*;
import com.repairverse.ai.dto.DevicePassportDto.DevicePassportData;
import com.repairverse.ai.dto.DevicePassportDto.DevicePassportResponse;
import com.repairverse.ai.exception.DeviceNotFoundException;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.DevicePassportService;
import com.repairverse.ai.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DeviceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeviceService deviceService;

    @MockBean
    private DevicePassportService devicePassportService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private DeviceDetailDto sampleDeviceDto;

    @BeforeEach
    void setUp() {
        sampleDeviceDto = DeviceDetailDto.builder()
                .id("dev-1")
                .userId("usr-123")
                .deviceName("Personal iPhone 14")
                .category("Smartphone")
                .brand("Apple")
                .model("iPhone 14")
                .currentCondition("Good")
                .createdAt("2026-08-16T12:00:00")
                .build();
    }

    @Test
    @DisplayName("GET /devices - 200 OK")
    void testGetUserDevices() throws Exception {
        DeviceListResponse response = DeviceListResponse.of(List.of(sampleDeviceDto));
        when(deviceService.getUserDevices(any())).thenReturn(response);

        mockMvc.perform(get("/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("dev-1"))
                .andExpect(jsonPath("$.data[0].deviceName").value("Personal iPhone 14"));
    }

    @Test
    @DisplayName("GET /devices/{id} - 200 OK on existing device")
    void testGetDeviceByIdSuccess() throws Exception {
        DeviceResponse response = DeviceResponse.of(sampleDeviceDto);
        when(deviceService.getDeviceById(eq("dev-1"), any())).thenReturn(response);

        mockMvc.perform(get("/devices/dev-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("dev-1"))
                .andExpect(jsonPath("$.data.brand").value("Apple"));
    }

    @Test
    @DisplayName("GET /devices/{id} - 404 Not Found on nonexistent device")
    void testGetDeviceByIdNotFound() throws Exception {
        when(deviceService.getDeviceById(eq("nonexistent"), any()))
                .thenThrow(new DeviceNotFoundException("Device not found with ID: nonexistent"));

        mockMvc.perform(get("/devices/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("DEVICE_NOT_FOUND"));
    }

    @Test
    @DisplayName("POST /devices - 201 Created on valid request")
    void testCreateDeviceSuccess() throws Exception {
        CreateDeviceRequest request = new CreateDeviceRequest(
                "Personal iPhone 14",
                "Smartphone",
                "Apple",
                "iPhone 14",
                "SN123",
                "2023-01-15",
                "2024-01-15",
                999.0,
                "Good"
        );

        DeviceResponse response = DeviceResponse.of("Device registered successfully", sampleDeviceDto);
        when(deviceService.createDevice(any(CreateDeviceRequest.class), any())).thenReturn(response);

        mockMvc.perform(post("/devices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("dev-1"));
    }

    @Test
    @DisplayName("POST /devices - 422 Unprocessable Entity on missing required fields")
    void testCreateDeviceValidationFailure() throws Exception {
        CreateDeviceRequest invalidRequest = new CreateDeviceRequest(
                "", // Blank name
                "", // Blank category
                "", // Blank brand
                "", // Blank model
                null, null, null, null, null
        );

        mockMvc.perform(post("/devices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("PUT /devices/{id} - 200 OK on update")
    void testUpdateDeviceSuccess() throws Exception {
        UpdateDeviceRequest updateRequest = new UpdateDeviceRequest(
                "Updated iPhone 14",
                null, null, null, null, null, null, null, "Excellent"
        );

        DeviceDetailDto updatedDto = DeviceDetailDto.builder()
                .id("dev-1")
                .userId("usr-123")
                .deviceName("Updated iPhone 14")
                .category("Smartphone")
                .brand("Apple")
                .model("iPhone 14")
                .currentCondition("Excellent")
                .build();

        DeviceResponse response = DeviceResponse.of("Device updated successfully", updatedDto);
        when(deviceService.updateDevice(eq("dev-1"), any(UpdateDeviceRequest.class), any())).thenReturn(response);

        mockMvc.perform(put("/devices/dev-1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deviceName").value("Updated iPhone 14"));
    }

    @Test
    @DisplayName("DELETE /devices/{id} - 200 OK on successful deletion")
    void testDeleteDeviceSuccess() throws Exception {
        doNothing().when(deviceService).deleteDevice(eq("dev-1"), any());

        mockMvc.perform(delete("/devices/dev-1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /devices/{id}/passport - 200 OK")
    void testGetDevicePassport() throws Exception {
        DeviceHealthDto healthDto = DeviceHealthDto.builder()
                .deviceId("dev-1")
                .healthScore(86)
                .batteryHealth(88)
                .build();

        DevicePassportData passportData = DevicePassportData.builder()
                .device(sampleDeviceDto)
                .health(healthDto)
                .lifecycleTimeline(Collections.emptyList())
                .build();

        DevicePassportResponse response = DevicePassportResponse.of(passportData);
        when(devicePassportService.getDevicePassport(eq("dev-1"), any())).thenReturn(response);

        mockMvc.perform(get("/devices/dev-1/passport"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.device.id").value("dev-1"))
                .andExpect(jsonPath("$.data.health.healthScore").value(86));
    }
}
