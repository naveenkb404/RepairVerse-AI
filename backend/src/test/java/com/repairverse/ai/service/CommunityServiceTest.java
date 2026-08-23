package com.repairverse.ai.service;

import com.repairverse.ai.dto.CommunityDto.*;
import com.repairverse.ai.entity.CommunityPost;
import com.repairverse.ai.entity.CommunityReply;
import com.repairverse.ai.entity.User;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.CommunityPostRepository;
import com.repairverse.ai.repository.CommunityReplyRepository;
import com.repairverse.ai.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityServiceTest {

    @Mock
    private CommunityPostRepository communityPostRepository;

    @Mock
    private CommunityReplyRepository communityReplyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommunityService communityService;

    @Test
    @DisplayName("Should return sample community posts when database is empty")
    void getAllPosts_SampleFallback() {
        when(communityPostRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        List<PostSummaryResponse> list = communityService.getAllPosts(null);

        assertThat(list).isNotEmpty();
        assertThat(list.get(0).id()).isEqualTo("post-001");
    }

    @Test
    @DisplayName("Should return live community posts when present")
    void getAllPosts_Live() {
        CommunityPost post = CommunityPost.builder()
                .id("p-1")
                .userId("usr-1")
                .authorName("Jane")
                .title("Question about Battery")
                .content("How to replace?")
                .category("Smartphone")
                .likesCount(5)
                .repliesCount(1)
                .createdAt(LocalDateTime.now())
                .build();

        when(communityPostRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(post));

        List<PostSummaryResponse> list = communityService.getAllPosts(null);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).title()).isEqualTo("Question about Battery");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when post not found")
    void getPostById_NotFound() {
        when(communityPostRepository.findById("p-none")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> communityService.getPostById("p-none"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create post successfully")
    void createPost_Success() {
        User user = User.builder().id("usr-1").fullName("Jane Doe").build();
        when(userRepository.findById("usr-1")).thenReturn(Optional.of(user));

        CommunityPost post = CommunityPost.builder()
                .id("p-new")
                .userId("usr-1")
                .authorName("Jane Doe")
                .title("New Topic")
                .content("Content")
                .category("General")
                .createdAt(LocalDateTime.now())
                .build();

        when(communityPostRepository.save(any(CommunityPost.class))).thenReturn(post);

        CreatePostRequest req = new CreatePostRequest("New Topic", "Content", "General", "iPhone 13");
        PostDetailResponse res = communityService.createPost("usr-1", req);

        assertThat(res.id()).isEqualTo("p-new");
        assertThat(res.title()).isEqualTo("New Topic");
    }

    @Test
    @DisplayName("Should add reply and increment replies count")
    void addReply_Success() {
        CommunityPost post = CommunityPost.builder()
                .id("p-1")
                .repliesCount(0)
                .build();

        when(communityPostRepository.findById("p-1")).thenReturn(Optional.of(post));

        CommunityReply reply = CommunityReply.builder()
                .id("rep-1")
                .authorName("Helper")
                .content("Here is how to do it")
                .createdAt(LocalDateTime.now())
                .build();

        when(communityReplyRepository.save(any(CommunityReply.class))).thenReturn(reply);

        ReplyResponse res = communityService.addReply("usr-2", "p-1", new CreateReplyRequest("Here is how to do it"));

        assertThat(res.id()).isEqualTo("rep-1");
        assertThat(post.getRepliesCount()).isEqualTo(1);
        verify(communityPostRepository, times(1)).save(post);
    }

    @Test
    @DisplayName("Should increment likes count on post")
    void likePost_Success() {
        CommunityPost post = CommunityPost.builder()
                .id("p-1")
                .likesCount(10)
                .build();

        when(communityPostRepository.findById("p-1")).thenReturn(Optional.of(post));

        int newLikes = communityService.likePost("p-1");

        assertThat(newLikes).isEqualTo(11);
        assertThat(post.getLikesCount()).isEqualTo(11);
    }
}
