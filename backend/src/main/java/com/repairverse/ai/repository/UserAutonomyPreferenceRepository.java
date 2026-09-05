package com.repairverse.ai.repository;

import com.repairverse.ai.entity.UserAutonomyPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAutonomyPreferenceRepository extends JpaRepository<UserAutonomyPreference, String> {

    Optional<UserAutonomyPreference> findByUserId(String userId);
}
