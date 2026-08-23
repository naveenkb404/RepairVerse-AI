package com.repairverse.ai.repository;

import com.repairverse.ai.entity.CommunityReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityReplyRepository extends JpaRepository<CommunityReply, String> {

    List<CommunityReply> findByPostIdOrderByCreatedAtAsc(String postId);
}
