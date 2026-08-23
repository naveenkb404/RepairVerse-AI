package com.repairverse.ai.controller;

import com.repairverse.ai.dto.CommunityDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Community Discussion & Q&A Hub REST Controller
 * Base path: /api/v1/community
 */
@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
@Slf4j
public class CommunityController {

    private final CommunityService communityService;

    /**
     * GET /api/v1/community/posts
     * List community discussions with optional category filtering
     */
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getAllPosts(
            @RequestParam(value = "category", required = false) String category) {
        List<PostSummaryResponse> posts = communityService.getAllPosts(category);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", posts,
                "message", "Community posts retrieved successfully"
        ));
    }

    /**
     * GET /api/v1/community/posts/{id}
     * Get discussion post details with reply thread
     */
    @GetMapping("/posts/{id}")
    public ResponseEntity<Map<String, Object>> getPostById(@PathVariable("id") String id) {
        PostDetailResponse post = communityService.getPostById(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", post,
                "message", "Community post retrieved successfully"
        ));
    }

    /**
     * POST /api/v1/community/posts
     * Create a new community discussion topic / repair question
     */
    @PostMapping("/posts")
    public ResponseEntity<Map<String, Object>> createPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CreatePostRequest request) {
        String userId = userPrincipal != null ? userPrincipal.getId() : "usr-123";
        PostDetailResponse post = communityService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "data", post,
                "message", "Community post published successfully"
        ));
    }

    /**
     * POST /api/v1/community/posts/{id}/reply
     * Add a reply or solution to a discussion post
     */
    @PostMapping("/posts/{id}/reply")
    public ResponseEntity<Map<String, Object>> addReply(
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CreateReplyRequest request) {
        String userId = userPrincipal != null ? userPrincipal.getId() : "usr-123";
        ReplyResponse reply = communityService.addReply(userId, id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "data", reply,
                "message", "Reply posted successfully"
        ));
    }

    /**
     * POST /api/v1/community/posts/{id}/like
     * Upvote/like a discussion post
     */
    @PostMapping("/posts/{id}/like")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable("id") String id) {
        int likes = communityService.likePost(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "likesCount", likes,
                "message", "Post upvoted successfully"
        ));
    }
}
