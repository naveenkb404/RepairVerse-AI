package com.repairverse.ai.repository;

import com.repairverse.ai.entity.MarketplaceInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketplaceInteractionRepository extends JpaRepository<MarketplaceInteraction, String> {

    List<MarketplaceInteraction> findByUserIdOrderByCreatedAtDesc(String userId);

    long countByUserIdAndInteractionType(String userId, String interactionType);

    long countByInteractionType(String interactionType);

    @Query("SELECT m.interactionType, COUNT(m) FROM MarketplaceInteraction m GROUP BY m.interactionType")
    List<Object[]> countGroupedByInteractionType();
}
