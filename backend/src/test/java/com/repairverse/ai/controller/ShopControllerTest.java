package com.repairverse.ai.controller;

import com.repairverse.ai.dto.ShopDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.RepairShopService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ShopController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepairShopService repairShopService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/shops - Public endpoint listing nearby shops")
    void getShops_PublicAccess() throws Exception {
        RepairShopResponse sample = new RepairShopResponse(
                "shop-001", "TechCare Express", "TechCare Express", "Owner",
                "742 Market St", 37.7865, -122.4045, 4.9, 328,
                "+1-555-0192", "info@techcare.io", "9am-7pm",
                List.of("Smartphone"), List.of("Smartphone Repair"), List.of("Apple"),
                "Same-Day", "$45", true, true, true, 0.8, true, true
        );

        ShopListResponse response = new ShopListResponse(true, "Shops loaded", List.of(sample));
        when(repairShopService.getShops(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/shops").param("latitude", "37.7865").param("longitude", "-122.4045"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].shopName").value("TechCare Express"));
    }

    @Test
    @DisplayName("GET /api/v1/shops/{id} - 404 Not Found when shop does not exist")
    void getShopById_NotFound() throws Exception {
        when(repairShopService.getShopById("nonexistent"))
                .thenThrow(new ResourceNotFoundException("Repair shop not found with ID: nonexistent"));

        mockMvc.perform(get("/shops/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
    }
}
