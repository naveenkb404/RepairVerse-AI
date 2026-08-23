package com.repairverse.ai.repository;

import com.repairverse.ai.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, String> {

    List<CommunityPost> findAllByOrderByCreatedAtDesc();

    List<CommunityPost> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category);

    List<CommunityPost> findByUserIdOrderByCreatedAtDesc(String userId);
}
