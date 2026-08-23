package com.repairverse.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.RepairGuideDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.RepairGuideService;
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

@WebMvcTest(controllers = RepairGuideController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RepairGuideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RepairGuideService repairGuideService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/repair-guide - Returns guide list")
    void getAllGuides_Success() throws Exception {
        GuideSummaryResponse summary = new GuideSummaryResponse("g-1", "OLED Replacement", "Smartphone", "Intermediate", "45m", "Tech", 100, 20, true, "2024-01-01");
        when(repairGuideService.getAllGuides(null, null)).thenReturn(List.of(summary));

        mockMvc.perform(get("/repair-guide"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("g-1"));
    }

    @Test
    @DisplayName("GET /api/v1/repair-guide/{id} - Returns guide details")
    void getGuideById_Success() throws Exception {
        GuideDetailResponse detail = new GuideDetailResponse("g-1", "OLED Replacement", "Smartphone", "Intermediate", "45m", "Content", "usr-1", "Tech", List.of(), List.of(), 100, 20, true, "2024-01-01");
        when(repairGuideService.getGuideById("g-1")).thenReturn(detail);

        mockMvc.perform(get("/repair-guide/g-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("OLED Replacement"));
    }

    @Test
    @DisplayName("POST /api/v1/repair-guide - Creates a new guide")
    void createGuide_Success() throws Exception {
        CreateGuideRequest req = new CreateGuideRequest("New Guide", "Smartphone", "Beginner", "20m", "Instructions", List.of(), List.of());
        GuideDetailResponse detail = new GuideDetailResponse("g-new", "New Guide", "Smartphone", "Beginner", "20m", "Instructions", "usr-123", "User", List.of(), List.of(), 0, 0, true, "2024-01-01");

        when(repairGuideService.createGuide(any(), any(), any(CreateGuideRequest.class))).thenReturn(detail);

        mockMvc.perform(post("/repair-guide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("g-new"));
    }
}
