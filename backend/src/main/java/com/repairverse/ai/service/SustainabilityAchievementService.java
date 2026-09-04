package com.repairverse.ai.service;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.entity.CircularImpactEvent;
import com.repairverse.ai.entity.SustainabilityAchievement;
import com.repairverse.ai.repository.CircularImpactEventRepository;
import com.repairverse.ai.repository.SustainabilityAchievementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Deterministic achievement calculation engine with duplicate unlock prevention.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SustainabilityAchievementService {

    private final SustainabilityAchievementRepository achievementRepository;
    private final CircularImpactEventRepository eventRepository;
    private final CircularImpactScoreService circularImpactScoreService;

    public record AchievementDefinition(
        String code,
        String name,
        String description,
        String requirement
    ) {}

    public static final List<AchievementDefinition> ALL_ACHIEVEMENTS = List.of(
        new AchievementDefinition("FIRST_REPAIR", "First Life Saved", "Successfully completed your first hardware diagnosis and repair.", "Complete 1 repair action"),
        new AchievementDefinition("EWASTE_SAVER", "E-Waste Guardian", "Prevented over 5kg of hazardous electronic scrap from entering landfills.", "Prevent 5kg e-waste"),
        new AchievementDefinition("CARBON_CONSCIOUS", "Carbon Conscious", "Offset over 25kg of carbon emissions through proactive repair and refurbishment.", "Save 25kg CO₂ emissions"),
        new AchievementDefinition("LIFE_EXTENDER", "Longevity Master", "Extended electronic hardware service lifespans by more than 180 cumulative days.", "Extend device lifespan by 180 days"),
        new AchievementDefinition("PLANET_PROTECTOR", "Planet Protector", "Prevented over 100kg of CO₂ emissions across your entire personal electronics fleet.", "Save 100kg CO₂ emissions"),
        new AchievementDefinition("CIRCULAR_CHAMPION", "Circular Champion", "Reached the pinnacle 90+ score tier in the Circular Economy Impact Index.", "Attain a Circular Impact Score >= 90")
    );

    @Transactional
    public List<SustainabilityAchievementDto> evaluateAchievements(String userId) {
        List<CircularImpactEvent> events = eventRepository.findByUserId(userId);
        double totalCarbon = 0.0;
        double totalEwaste = 0.0;
        int totalLifeDays = 0;
        long totalRepairs = 0;

        for (CircularImpactEvent e : events) {
            totalCarbon += (e.getCarbonSavedKg() != null ? e.getCarbonSavedKg() : 0.0);
            totalEwaste += (e.getEwastePreventedKg() != null ? e.getEwastePreventedKg() : 0.0);
            totalLifeDays += (e.getDeviceLifeExtensionDays() != null ? e.getDeviceLifeExtensionDays() : 0);
            if (e.getEventType() != null && e.getEventType().toUpperCase().contains("REPAIR")) {
                totalRepairs++;
            }
        }

        CircularImpactScoreDto scoreDto = circularImpactScoreService.calculateScore(userId);

        // Check & unlock First Repair
        if (totalRepairs >= 1) {
            unlockIfNotExists(userId, "FIRST_REPAIR", "First Life Saved",
                "Successfully completed your first hardware diagnosis and repair.", (double) totalRepairs);
        }

        // Check & unlock E-Waste Saver
        if (totalEwaste >= 5.0) {
            unlockIfNotExists(userId, "EWASTE_SAVER", "E-Waste Guardian",
                "Prevented over 5kg of hazardous electronic scrap from entering landfills.", totalEwaste);
        }

        // Check & unlock Carbon Conscious
        if (totalCarbon >= 25.0) {
            unlockIfNotExists(userId, "CARBON_CONSCIOUS", "Carbon Conscious",
                "Offset over 25kg of carbon emissions through proactive repair and refurbishment.", totalCarbon);
        }

        // Check & unlock Life Extender
        if (totalLifeDays >= 180) {
            unlockIfNotExists(userId, "LIFE_EXTENDER", "Longevity Master",
                "Extended electronic hardware service lifespans by more than 180 cumulative days.", (double) totalLifeDays);
        }

        // Check & unlock Planet Protector
        if (totalCarbon >= 100.0) {
            unlockIfNotExists(userId, "PLANET_PROTECTOR", "Planet Protector",
                "Prevented over 100kg of CO₂ emissions across your entire personal electronics fleet.", totalCarbon);
        }

        // Check & unlock Circular Champion
        if (scoreDto.score() >= 90) {
            unlockIfNotExists(userId, "CIRCULAR_CHAMPION", "Circular Champion",
                "Reached the pinnacle 90+ score tier in the Circular Economy Impact Index.", (double) scoreDto.score());
        }

        return getUserAchievements(userId);
    }

    @Transactional(readOnly = true)
    public List<SustainabilityAchievementDto> getUserAchievements(String userId) {
        List<SustainabilityAchievement> unlocked = achievementRepository.findByUserIdOrderByUnlockedAtDesc(userId);
        Map<String, SustainabilityAchievement> unlockedMap = new HashMap<>();
        for (SustainabilityAchievement a : unlocked) {
            unlockedMap.put(a.getAchievementCode(), a);
        }

        List<SustainabilityAchievementDto> result = new ArrayList<>();
        for (AchievementDefinition def : ALL_ACHIEVEMENTS) {
            SustainabilityAchievement sa = unlockedMap.get(def.code());
            boolean isUnlocked = sa != null;
            result.add(new SustainabilityAchievementDto(
                isUnlocked ? sa.getId() : "locked-" + def.code(),
                def.code(),
                def.name(),
                def.description(),
                isUnlocked,
                isUnlocked ? sa.getUnlockedAt() : null,
                isUnlocked ? sa.getImpactValue() : 0.0,
                def.requirement()
            ));
        }

        return result;
    }

    private void unlockIfNotExists(String userId, String code, String name, String desc, Double impactValue) {
        if (!achievementRepository.existsByUserIdAndAchievementCode(userId, code)) {
            SustainabilityAchievement sa = SustainabilityAchievement.builder()
                .userId(userId)
                .achievementCode(code)
                .achievementName(name)
                .achievementDescription(desc)
                .unlockedAt(LocalDateTime.now())
                .impactValue(impactValue != null ? impactValue : 0.0)
                .build();
            achievementRepository.save(sa);
            log.info("Unlocked achievement '{}' [{}] for user '{}'", name, code, userId);
        }
    }
}
