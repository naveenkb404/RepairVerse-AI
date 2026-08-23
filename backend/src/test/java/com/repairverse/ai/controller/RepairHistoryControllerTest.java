package com.repairverse.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.RepairHistoryDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.RepairHistoryService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RepairHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RepairHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RepairHistoryService repairHistoryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/repair-history - Returns list of repairs")
    void getRepairHistory_Success() throws Exception {
        RepairDeviceSummary dev = new RepairDeviceSummary("dev-1", "iPhone 13", "Apple", "13", "Smartphone", "SN1");
        RepairHistoryItemResponse rep = new RepairHistoryItemResponse(
                "rep-101", "dev-1", dev, "Screen Fix", "2026-08-01", "Completed",
                "Fixed glass", "Cracked", 90, null, null, List.of(),
                50.0, 30.0, 80.0, "2 hours", "1 Year", "2027-08-01", true,
                20.0, 0.2, 200.0, "Notes", List.of()
        );

        when(repairHistoryService.getRepairHistoryForUser("usr-123")).thenReturn(List.of(rep));

        mockMvc.perform(get("/repair-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("rep-101"))
                .andExpect(jsonPath("$.data[0].repairType").value("Screen Fix"));
    }

    @Test
    @DisplayName("GET /api/v1/repair-history/{id} - Returns single repair details")
    void getRepairHistoryById_Success() throws Exception {
        RepairDeviceSummary dev = new RepairDeviceSummary("dev-1", "iPhone 13", "Apple", "13", "Smartphone", "SN1");
        RepairHistoryItemResponse rep = new RepairHistoryItemResponse(
                "rep-101", "dev-1", dev, "Screen Fix", "2026-08-01", "Completed",
                "Fixed glass", "Cracked", 90, null, null, List.of(),
                50.0, 30.0, 80.0, "2 hours", "1 Year", "2027-08-01", true,
                20.0, 0.2, 200.0, "Notes", List.of()
        );

        when(repairHistoryService.getRepairHistoryById("usr-123", "rep-101")).thenReturn(rep);

        mockMvc.perform(get("/repair-history/rep-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("rep-101"));
    }

    @Test
    @DisplayName("POST /api/v1/repair-history - Creates a new repair record")
    void createRepairRecord_Success() throws Exception {
        CreateRepairHistoryRequest request = new CreateRepairHistoryRequest(
                "dev-1", "Port Repair", "2026-08-22", "Completed",
                "Cleaned lightning port", "No Charge", 98, "Tech B", "Level 2",
                "Shop B", "456 St", List.of(), 10.0, 20.0, 30.0, "30 mins",
                "30 Days", "2026-09-22", true, 5.0, 0.05, 50.0, "Cleaned", List.of()
        );

        RepairDeviceSummary dev = new RepairDeviceSummary("dev-1", "iPhone 13", "Apple", "13", "Smartphone", "SN1");
        RepairHistoryItemResponse created = new RepairHistoryItemResponse(
                "rep-new", "dev-1", dev, "Port Repair", "2026-08-22", "Completed",
                "Cleaned lightning port", "No Charge", 98, null, null, List.of(),
                10.0, 20.0, 30.0, "30 mins", "30 Days", "2026-09-22", true,
                5.0, 0.05, 50.0, "Cleaned", List.of()
        );

        when(repairHistoryService.createRepairRecord(eq("usr-123"), any(CreateRepairHistoryRequest.class))).thenReturn(created);

        mockMvc.perform(post("/repair-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("rep-new"));
    }
}
