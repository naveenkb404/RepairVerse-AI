package com.repairverse.ai.controller;

import com.repairverse.ai.dto.NotificationDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/notifications - Return notifications list")
    void getUserNotifications() throws Exception {
        UserPrincipal principal = new UserPrincipal("usr-123", "User", "user@example.com", "pass", Collections.emptyList());
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        NotificationResponse item = new NotificationResponse(
                "notif-1", "usr-123", "repair", "Repair Completed", "Your repair is ready", false, "/repair-history", "View", "green", "2026-08-20T10:00:00"
        );
        NotificationListResponse response = new NotificationListResponse(true, "Fetched", List.of(item));

        when(notificationService.getUserNotifications("usr-123")).thenReturn(response);

        mockMvc.perform(get("/notifications").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Repair Completed"));
    }

    @Test
    @DisplayName("PUT /api/v1/notifications/{id}/read - Mark notification read")
    void markAsRead() throws Exception {
        UserPrincipal principal = new UserPrincipal("usr-123", "User", "user@example.com", "pass", Collections.emptyList());
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        doNothing().when(notificationService).markAsRead("notif-1", "usr-123");

        mockMvc.perform(put("/notifications/notif-1/read").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
