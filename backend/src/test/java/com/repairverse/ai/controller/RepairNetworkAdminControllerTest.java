package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairNetworkIntelligenceDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.repository.MarketplaceAnomalyRepository;
import com.repairverse.ai.repository.RepairShopQualitySnapshotRepository;
import com.repairverse.ai.repository.RepairServiceOutcomeRepository;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RepairNetworkAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RepairNetworkAdminControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean RepairNetworkRankingService        rankingService;
    @MockBean MarketplaceAnomalyDetectionService anomalyService;
    @MockBean RepairTrustIntelligenceService     trustService;
    @MockBean RepairShopQualitySnapshotRepository snapshotRepository;
    @MockBean MarketplaceAnomalyRepository       anomalyRepository;
    @MockBean RepairServiceOutcomeRepository     outcomeRepository;
    @MockBean JwtTokenProvider                   jwtTokenProvider;
    @MockBean CustomUserDetailsService           customUserDetailsService;

    @Test
    void getAnomalies_returnsOpenAnomalies() throws Exception {
        when(anomalyService.getAnomalies(null, null)).thenReturn(List.of(
            new MarketplaceAnomalyResponse(
                "anom-1", "shop-1", "Test Shop",
                "LOW_SUCCESS_RATE", "HIGH", 75,
                "Success rate below threshold", "OPEN", LocalDateTime.now()
            )
        ));

        mockMvc.perform(get("/api/v1/admin/network-intelligence/anomalies"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].anomalyType").value("LOW_SUCCESS_RATE"))
            .andExpect(jsonPath("$.data[0].severity").value("HIGH"));
    }

    @Test
    void updateAnomalyStatus_validTransition_returns200() throws Exception {
        MarketplaceAnomalyResponse updated = new MarketplaceAnomalyResponse(
            "anom-1", "shop-1", "Test Shop",
            "REVIEW_SPIKE", "MEDIUM", 55,
            "Review spike detected", "UNDER_REVIEW", LocalDateTime.now()
        );
        when(anomalyService.updateAnomalyStatus("anom-1", "UNDER_REVIEW")).thenReturn(updated);

        UpdateAnomalyStatusRequest req = new UpdateAnomalyStatusRequest("UNDER_REVIEW");
        mockMvc.perform(put("/api/v1/admin/network-intelligence/anomalies/anom-1/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("UNDER_REVIEW"));
    }
}
