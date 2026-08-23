package com.repairverse.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.RepairGuideDto.*;
import com.repairverse.ai.entity.RepairGuide;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.RepairGuideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairGuideService {

    private final RepairGuideRepository repairGuideRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<GuideSummaryResponse> getAllGuides(String category, String difficulty) {
        List<RepairGuide> guides;
        if (category != null && !category.isBlank()) {
            guides = repairGuideRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(category);
        } else if (difficulty != null && !difficulty.isBlank()) {
            guides = repairGuideRepository.findByDifficultyIgnoreCaseOrderByCreatedAtDesc(difficulty);
        } else {
            guides = repairGuideRepository.findAllByOrderByCreatedAtDesc();
        }

        if (guides.isEmpty()) {
            log.info("No guides in database. Returning sample curated reference guides.");
            return getSampleGuides().stream().map(this::mapToSummary).collect(Collectors.toList());
        }

        return guides.stream().map(this::mapToSummary).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GuideDetailResponse getGuideById(String id) {
        RepairGuide guide = repairGuideRepository.findById(id).orElse(null);
        if (guide != null) {
            return mapToDetail(guide);
        }

        return getSampleGuides().stream()
                .filter(g -> g.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Repair guide not found with id: " + id));
    }

    @Transactional
    public GuideDetailResponse createGuide(String userId, String authorName, CreateGuideRequest request) {
        String toolsJson = null;
        String stepsJson = null;
        try {
            if (request.tools() != null) {
                toolsJson = objectMapper.writeValueAsString(request.tools());
            }
            if (request.steps() != null) {
                stepsJson = objectMapper.writeValueAsString(request.steps());
            }
        } catch (Exception e) {
            log.warn("Failed to serialize guide tools/steps JSON: {}", e.getMessage());
        }

        RepairGuide guide = RepairGuide.builder()
                .id("guide-" + UUID.randomUUID().toString().substring(0, 8))
                .title(request.title())
                .category(request.category())
                .difficulty(request.difficulty())
                .estimatedTime(request.estimatedTime())
                .guideContent(request.guideContent())
                .authorId(userId)
                .authorName(authorName != null ? authorName : "Community Contributor")
                .toolsJson(toolsJson)
                .stepsJson(stepsJson)
                .viewsCount(1)
                .likesCount(0)
                .isVerified(true)
                .createdAt(LocalDateTime.now())
                .build();

        RepairGuide saved = repairGuideRepository.save(guide);
        log.info("Repair guide created id='{}' title='{}'", saved.getId(), saved.getTitle());
        return mapToDetail(saved);
    }

    private GuideSummaryResponse mapToSummary(RepairGuide g) {
        String createdAt = g.getCreatedAt() != null 
                ? g.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE) 
                : "2024-01-01";
        return new GuideSummaryResponse(
                g.getId(),
                g.getTitle(),
                g.getCategory(),
                g.getDifficulty(),
                g.getEstimatedTime(),
                g.getAuthorName() != null ? g.getAuthorName() : "RepairVerse Expert",
                g.getViewsCount() != null ? g.getViewsCount() : 120,
                g.getLikesCount() != null ? g.getLikesCount() : 45,
                g.getIsVerified() != null ? g.getIsVerified() : true,
                createdAt
        );
    }

    private GuideSummaryResponse mapToSummary(GuideDetailResponse d) {
        return new GuideSummaryResponse(
                d.id(),
                d.title(),
                d.category(),
                d.difficulty(),
                d.estimatedTime(),
                d.authorName(),
                d.viewsCount(),
                d.likesCount(),
                d.isVerified(),
                d.createdAt()
        );
    }

    private GuideDetailResponse mapToDetail(RepairGuide g) {
        List<ToolItem> tools = parseToolsJson(g.getToolsJson());
        List<StepItem> steps = parseStepsJson(g.getStepsJson());
        String createdAt = g.getCreatedAt() != null 
                ? g.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE) 
                : "2024-01-01";

        return new GuideDetailResponse(
                g.getId(),
                g.getTitle(),
                g.getCategory(),
                g.getDifficulty(),
                g.getEstimatedTime(),
                g.getGuideContent(),
                g.getAuthorId(),
                g.getAuthorName(),
                tools,
                steps,
                g.getViewsCount() != null ? g.getViewsCount() : 150,
                g.getLikesCount() != null ? g.getLikesCount() : 60,
                g.getIsVerified() != null ? g.getIsVerified() : true,
                createdAt
        );
    }

    private List<ToolItem> parseToolsJson(String json) {
        if (json == null || json.isBlank()) return getDefaultTools();
        try {
            return objectMapper.readValue(json, new TypeReference<List<ToolItem>>() {});
        } catch (Exception e) {
            return getDefaultTools();
        }
    }

    private List<StepItem> parseStepsJson(String json) {
        if (json == null || json.isBlank()) return getDefaultSteps();
        try {
            return objectMapper.readValue(json, new TypeReference<List<StepItem>>() {});
        } catch (Exception e) {
            return getDefaultSteps();
        }
    }

    private List<ToolItem> getDefaultTools() {
        return List.of(
                new ToolItem("P2 Pentalobe Screwdriver", "Required to remove bottom perimeter screws", true),
                new ToolItem("Anti-Static Precision Tweezers", "Safe handling of micro-connectors", true),
                new ToolItem("Suction Handle & Plastic Spudger", "Screen separation without frame marring", true),
                new ToolItem("Heat Gun or iOpener", "Softens display waterproofing adhesive", false)
        );
    }

    private List<StepItem> getDefaultSteps() {
        return List.of(
                new StepItem(1, "Power Down & Remove Pentalobe Screws", "Switch off the device completely. Remove the two 6.7mm pentalobe screws located at the bottom edge.", "Never puncture battery cells with sharp tools.", null),
                new StepItem(2, "Apply Thermal Heat & Lift Display", "Heat perimeter with 80°C for 90 seconds. Use suction cup to create gap and insert guitar pick.", "Do not insert pick deeper than 3mm to prevent severing flex cables.", null),
                new StepItem(3, "Disconnect Battery & Display Cables", "Remove connector cowlings. Use plastic spudger to disconnect battery connector first before display cables.", "Always disconnect battery first to prevent board shorts.", null),
                new StepItem(4, "Install Replacement Component & Reseal", "Seat new OEM component, connect flex cables, apply new perimeter adhesive gasket, and reassemble.", "Ensure all screws return to their exact original slots.", null)
        );
    }

    private List<GuideDetailResponse> getSampleGuides() {
        return List.of(
                new GuideDetailResponse(
                        "guide-001",
                        "iPhone 13 / 14 Pro OLED Display & Digitizer Replacement",
                        "Smartphone",
                        "Intermediate",
                        "45 mins",
                        "Comprehensive step-by-step procedure for disassembling and replacing front display assembly with True Tone transfer.",
                        "auth-exp-1",
                        "Alex Vance, Master Tech",
                        getDefaultTools(),
                        getDefaultSteps(),
                        1420,
                        320,
                        true,
                        "2024-02-01"
                ),
                new GuideDetailResponse(
                        "guide-002",
                        "MacBook Pro 14 / 16 Battery Pack Replacement & Thermal Clean",
                        "Laptop",
                        "Advanced",
                        "1 hour 30 mins",
                        "Full disassembly guide for removing adhesive battery cells and repasting M-series heatsink with thermal compound.",
                        "auth-exp-2",
                        "Elena Rostova, Mac Specialist",
                        getDefaultTools(),
                        getDefaultSteps(),
                        980,
                        210,
                        true,
                        "2024-03-15"
                ),
                new GuideDetailResponse(
                        "guide-003",
                        "PlayStation 5 Liquid Metal Thermal Paste & Fan Replacement",
                        "Console",
                        "Advanced",
                        "1 hour 15 mins",
                        "Complete deep clean guide covering power supply dust traps, fan bearing replacement, and APU liquid metal barrier inspection.",
                        "auth-exp-3",
                        "Marcus Cole, Hardware Engineer",
                        getDefaultTools(),
                        getDefaultSteps(),
                        830,
                        195,
                        true,
                        "2024-04-10"
                )
        );
    }
}
