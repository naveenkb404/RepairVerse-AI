package com.repairverse.ai.service;

import com.repairverse.ai.dto.CommunityDto.*;
import com.repairverse.ai.entity.CommunityPost;
import com.repairverse.ai.entity.CommunityReply;
import com.repairverse.ai.entity.User;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.CommunityPostRepository;
import com.repairverse.ai.repository.CommunityReplyRepository;
import com.repairverse.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityReplyRepository communityReplyRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PostSummaryResponse> getAllPosts(String category) {
        List<CommunityPost> posts;
        if (category != null && !category.isBlank()) {
            posts = communityPostRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(category);
        } else {
            posts = communityPostRepository.findAllByOrderByCreatedAtDesc();
        }

        if (posts.isEmpty()) {
            log.info("No community posts found in database. Returning sample curated community posts.");
            return getSamplePosts();
        }

        return posts.stream().map(this::mapToSummary).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostById(String id) {
        CommunityPost post = communityPostRepository.findById(id).orElse(null);
        if (post != null) {
            List<CommunityReply> replies = communityReplyRepository.findByPostIdOrderByCreatedAtAsc(post.getId());
            return mapToDetail(post, replies);
        }

        return getSamplePostsDetail().stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Community post not found with id: " + id));
    }

    @Transactional
    public PostDetailResponse createPost(String userId, CreatePostRequest request) {
        User user = userRepository.findById(userId).orElse(null);
        String authorName = user != null ? user.getFullName() : "Community Member";
        String avatar = user != null && user.getProfileImage() != null ? user.getProfileImage() : "https://images.unsplash.com/photo-1534528741775-53994a69daeb";

        CommunityPost post = CommunityPost.builder()
                .id("post-" + UUID.randomUUID().toString().substring(0, 8))
                .userId(userId)
                .authorName(authorName)
                .authorAvatar(avatar)
                .title(request.title())
                .content(request.content())
                .category(request.category() != null ? request.category() : "General")
                .deviceModel(request.deviceModel())
                .likesCount(0)
                .repliesCount(0)
                .isSolved(false)
                .createdAt(LocalDateTime.now())
                .build();

        CommunityPost saved = communityPostRepository.save(post);
        log.info("Community post created id='{}' by user '{}'", saved.getId(), userId);
        return mapToDetail(saved, List.of());
    }

    @Transactional
    public ReplyResponse addReply(String userId, String postId, CreateReplyRequest request) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User user = userRepository.findById(userId).orElse(null);
        String authorName = user != null ? user.getFullName() : "Community Member";
        String avatar = user != null && user.getProfileImage() != null ? user.getProfileImage() : "https://images.unsplash.com/photo-1534528741775-53994a69daeb";

        CommunityReply reply = CommunityReply.builder()
                .id("reply-" + UUID.randomUUID().toString().substring(0, 8))
                .post(post)
                .userId(userId)
                .authorName(authorName)
                .authorAvatar(avatar)
                .content(request.content())
                .isSolution(false)
                .likesCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        CommunityReply saved = communityReplyRepository.save(reply);
        post.setRepliesCount(post.getRepliesCount() + 1);
        communityPostRepository.save(post);

        log.info("Reply added id='{}' to post id='{}'", saved.getId(), postId);
        return mapToReplyDto(saved);
    }

    @Transactional
    public int likePost(String postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        post.setLikesCount(post.getLikesCount() + 1);
        communityPostRepository.save(post);
        return post.getLikesCount();
    }

    private PostSummaryResponse mapToSummary(CommunityPost p) {
        String snippet = p.getContent() != null && p.getContent().length() > 140
                ? p.getContent().substring(0, 140) + "..."
                : p.getContent();

        String createdAt = p.getCreatedAt() != null 
                ? p.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE) 
                : "2024-01-01";

        return new PostSummaryResponse(
                p.getId(),
                p.getAuthorName(),
                p.getAuthorAvatar(),
                p.getTitle(),
                snippet,
                p.getCategory(),
                p.getDeviceModel(),
                p.getLikesCount(),
                p.getRepliesCount(),
                p.getIsSolved(),
                createdAt
        );
    }

    private PostDetailResponse mapToDetail(CommunityPost p, List<CommunityReply> replies) {
        String createdAt = p.getCreatedAt() != null 
                ? p.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE) 
                : "2024-01-01";

        List<ReplyResponse> replyDtos = replies.stream().map(this::mapToReplyDto).collect(Collectors.toList());

        return new PostDetailResponse(
                p.getId(),
                p.getAuthorName(),
                p.getAuthorAvatar(),
                p.getTitle(),
                p.getContent(),
                p.getCategory(),
                p.getDeviceModel(),
                p.getLikesCount(),
                p.getRepliesCount(),
                p.getIsSolved(),
                createdAt,
                replyDtos
        );
    }

    private ReplyResponse mapToReplyDto(CommunityReply r) {
        String createdAt = r.getCreatedAt() != null 
                ? r.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) 
                : "2024-01-01T00:00:00";
        return new ReplyResponse(
                r.getId(),
                r.getAuthorName(),
                r.getAuthorAvatar(),
                r.getContent(),
                r.getIsSolution(),
                r.getLikesCount(),
                createdAt
        );
    }

    private List<PostSummaryResponse> getSamplePosts() {
        return List.of(
                new PostSummaryResponse(
                        "post-001", "David Kim", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                        "Successful iPhone 14 Pro OLED & Battery DIY Repair with True Tone Transfer",
                        "Just finished replacing my shattered display. Used the RepairVerse guide and an EEPROM programmer to retain True Tone...",
                        "Smartphone", "iPhone 14 Pro", 38, 12, true, "2024-02-14"
                ),
                new PostSummaryResponse(
                        "post-002", "Sarah Jenkins", "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                        "MacBook Pro 16 M1 Liquid Metal vs PTM7950 Thermal Pad Results",
                        "Did a side-by-side benchmark test after removing degraded factory paste. Idle temps dropped by 14°C under heavy compile workloads.",
                        "Laptop", "MacBook Pro 16", 52, 19, true, "2024-03-01"
                ),
                new PostSummaryResponse(
                        "post-003", "Carlos Mendes", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                        "PS5 HDMI Port Replacement — Recommended Solder Flux and Temperature?",
                        "What hot air station nozzle temperature do you recommend when removing the HDMI port on PS5 CFI-1200 motherboard without lifting traces?",
                        "Console", "PlayStation 5", 24, 8, false, "2024-03-10"
                )
        );
    }

    private List<PostDetailResponse> getSamplePostsDetail() {
        List<ReplyResponse> replies1 = List.of(
                new ReplyResponse("rep-1", "Elena Rostova", "https://images.unsplash.com/photo-1534528741775-53994a69daeb", "Great job preserving the ambient light sensor! Did you apply the pre-cut waterproof gasket as well?", false, 6, "2024-02-14T14:30:00"),
                new ReplyResponse("rep-2", "David Kim", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d", "Yes! The pre-cut adhesive seal seated cleanly using an alignment bracket.", true, 4, "2024-02-14T15:00:00")
        );

        return List.of(
                new PostDetailResponse(
                        "post-001", "David Kim", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                        "Successful iPhone 14 Pro OLED & Battery DIY Repair with True Tone Transfer",
                        "Just finished replacing my shattered display. Used the RepairVerse guide and an EEPROM programmer to retain True Tone. Total repair took around 50 minutes and saved over $280 compared to Apple Store out-of-warranty quote.",
                        "Smartphone", "iPhone 14 Pro", 38, 2, true, "2024-02-14", replies1
                )
        );
    }
}
