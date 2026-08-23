package com.repairverse.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.UserProfileDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/users/profile - Returns user profile")
    void getProfile_Success() throws Exception {
        ProfileResponse profile = new ProfileResponse(
                "usr-123", "Jane Doe", "jane@example.com", "USER",
                "avatar.jpg", "+123456", "San Francisco, CA", "Bio",
                "2024-01-01", "Now", true, new UserPreferencesDto(true, true, "dark", "en"),
                4, 9, 47.3, 1240.0
        );

        when(userService.getUserProfile("usr-123")).thenReturn(profile);

        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Jane Doe"))
                .andExpect(jsonPath("$.data.totalDevices").value(4));
    }

    @Test
    @DisplayName("PUT /api/v1/users/profile - Updates profile successfully")
    void updateProfile_Success() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest("Jane Updated", "+987654", "Seattle, WA", "New Bio", "new_avatar.jpg");
        ProfileResponse updatedProfile = new ProfileResponse(
                "usr-123", "Jane Updated", "jane@example.com", "USER",
                "new_avatar.jpg", "+987654", "Seattle, WA", "New Bio",
                "2024-01-01", "Now", true, new UserPreferencesDto(true, true, "dark", "en"),
                4, 9, 47.3, 1240.0
        );

        when(userService.updateUserProfile(eq("usr-123"), any(UpdateProfileRequest.class))).thenReturn(updatedProfile);

        mockMvc.perform(put("/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Jane Updated"))
                .andExpect(jsonPath("$.data.location").value("Seattle, WA"));
    }
}
