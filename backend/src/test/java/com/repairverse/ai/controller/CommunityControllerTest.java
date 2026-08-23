package com.repairverse.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.CommunityDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.CommunityService;
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

@WebMvcTest(controllers = CommunityController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommunityService communityService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/community/posts - Returns posts list")
    void getAllPosts_Success() throws Exception {
        PostSummaryResponse p = new PostSummaryResponse("p-1", "Jane", "avatar.jpg", "Title", "Snippet", "Smartphone", "iPhone 13", 5, 2, false, "2024-01-01");
        when(communityService.getAllPosts(null)).thenReturn(List.of(p));

        mockMvc.perform(get("/community/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("p-1"));
    }

    @Test
    @DisplayName("GET /api/v1/community/posts/{id} - Returns post detail")
    void getPostById_Success() throws Exception {
        PostDetailResponse p = new PostDetailResponse("p-1", "Jane", "avatar.jpg", "Title", "Full content", "Smartphone", "iPhone 13", 5, 2, false, "2024-01-01", List.of());
        when(communityService.getPostById("p-1")).thenReturn(p);

        mockMvc.perform(get("/community/posts/p-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Title"));
    }

    @Test
    @DisplayName("POST /api/v1/community/posts - Creates a new post")
    void createPost_Success() throws Exception {
        CreatePostRequest req = new CreatePostRequest("New Title", "New Content", "Smartphone", "iPhone 13");
        PostDetailResponse p = new PostDetailResponse("p-new", "Jane", "avatar.jpg", "New Title", "New Content", "Smartphone", "iPhone 13", 0, 0, false, "2024-01-01", List.of());

        when(communityService.createPost(any(), any(CreatePostRequest.class))).thenReturn(p);

        mockMvc.perform(post("/community/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("p-new"));
    }

    @Test
    @DisplayName("POST /api/v1/community/posts/{id}/reply - Adds reply to post")
    void addReply_Success() throws Exception {
        CreateReplyRequest req = new CreateReplyRequest("Helpful reply");
        ReplyResponse r = new ReplyResponse("r-1", "Helper", "avatar.jpg", "Helpful reply", false, 0, "2024-01-01");

        when(communityService.addReply(any(), eq("p-1"), any(CreateReplyRequest.class))).thenReturn(r);

        mockMvc.perform(post("/community/posts/p-1/reply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("r-1"));
    }

    @Test
    @DisplayName("POST /api/v1/community/posts/{id}/like - Likes a post")
    void likePost_Success() throws Exception {
        when(communityService.likePost("p-1")).thenReturn(15);

        mockMvc.perform(post("/community/posts/p-1/like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.likesCount").value(15));
    }
}
