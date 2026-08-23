package com.repairverse.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.RepairCostDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.RepairCostEstimatorService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RepairCostEstimatorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RepairCostEstimatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RepairCostEstimatorService repairCostEstimatorService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/v1/repair-cost-estimate - Returns cost estimate breakdown")
    void calculateCostEstimate_Success() throws Exception {
        CostOption diy = new CostOption("DIY", "Self repair", 100, 0, 100, "1h", "90d", "Best Value");
        CostOption local = new CostOption("Local", "Local tech", 100, 45, 145, "2h", "6m", "Popular");
        CostOption auth = new CostOption("Auth", "Official", 150, 95, 245, "3d", "1y", "Guaranteed");

        CostEstimateResponse res = new CostEstimateResponse("Smartphone", "iPhone 13", "Screen", 900, diy, local, auth, 800, 88, "Recommended", List.of("Screen"));
        when(repairCostEstimatorService.calculateEstimate(any(CostEstimateRequest.class))).thenReturn(res);

        CostEstimateRequest req = new CostEstimateRequest("Smartphone", "iPhone 13", "Screen", "1");

        mockMvc.perform(post("/repair-cost-estimate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.category").value("Smartphone"))
                .andExpect(jsonPath("$.data.maxSavingsDollars").value(800));
    }

    @Test
    @DisplayName("GET /api/v1/repair-cost-estimate/categories - Returns supported categories")
    void getSupportedCategories_Success() throws Exception {
        CategoryIssueBaseline cat = new CategoryIssueBaseline("Smartphone", List.of("Screen", "Battery"));
        when(repairCostEstimatorService.getSupportedCategories()).thenReturn(List.of(cat));

        mockMvc.perform(get("/repair-cost-estimate/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].category").value("Smartphone"));
    }
}
