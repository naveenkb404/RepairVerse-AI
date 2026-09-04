package com.repairverse.ai.service;

import com.repairverse.ai.entity.AutonomousActionPlan;
import com.repairverse.ai.entity.AutonomousActionStep;
import com.repairverse.ai.entity.AutonomousIntervention;
import com.repairverse.ai.repository.AutonomousActionPlanRepository;
import com.repairverse.ai.repository.AutonomousActionStepRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutonomousActionPlanningService {

    private final AutonomousActionPlanRepository planRepository;
    private final AutonomousActionStepRepository stepRepository;

    @Transactional
    public AutonomousActionPlan generateAndSavePlan(AutonomousIntervention intervention) {
        log.info("Generating autonomous action plan for intervention '{}' [{}]", intervention.getId(), intervention.getInterventionType());

        // Delete existing plan if any
        planRepository.deleteByInterventionId(intervention.getId());

        String interventionType = intervention.getInterventionType();
        String title = intervention.getTitle();

        AutonomousActionPlan plan = AutonomousActionPlan.builder()
                .interventionId(intervention.getId())
                .planName("Action Plan: " + title)
                .objective(determineObjective(interventionType, intervention.getDescription()))
                .status("PLANNED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        AutonomousActionPlan savedPlan = planRepository.save(plan);

        List<AutonomousActionStep> steps = buildSteps(savedPlan.getId(), interventionType, intervention);
        for (AutonomousActionStep step : steps) {
            step.setPlan(savedPlan);
            stepRepository.save(step);
        }

        savedPlan.setTotalSteps(steps.size());
        savedPlan.setCompletedSteps(0);
        savedPlan.setSteps(steps);

        return planRepository.save(savedPlan);
    }

    private String determineObjective(String interventionType, String description) {
        return switch (interventionType) {
            case "URGENT_REPAIR", "PREVENTIVE_REPAIR" -> "Coordinate diagnostic confirmation, match certified repair shops, and schedule component repair to restore device health.";
            case "MAINTENANCE" -> "Execute preventative cleaning, battery calibration, and thermal compound servicing to prevent component failure.";
            case "PROFESSIONAL_SERVICE" -> "Engage certified technician diagnostic evaluation to resolve complex or safety-critical hardware faults.";
            case "REFURBISH" -> "Plan and execute core module upgrades (battery/storage) to extend functional utility by 24+ months.";
            case "REPLACE" -> "Structure responsible phased device upgrade plan and trade-in pathway.";
            case "RECYCLE" -> "Facilitate certified zero-landfill e-waste circular recycling with precious metal recovery.";
            case "SHOP_RECOMMENDATION", "QUOTE_REQUEST" -> "Identify top trusted marketplace repair shops and negotiate transparent service pricing.";
            case "DEVICE_OPTIMIZATION" -> "Calibrate OS telemetry, power throttling, and background cycles for longevity preservation.";
            default -> "Continuously monitor hardware metrics and alert user on telemetry divergence.";
        };
    }

    private List<AutonomousActionStep> buildSteps(String planId, String type, AutonomousIntervention intervention) {
        List<AutonomousActionStep> steps = new ArrayList<>();

        switch (type) {
            case "URGENT_REPAIR", "PREVENTIVE_REPAIR" -> {
                steps.add(createStep(planId, 1, "GENERATE_REPORT", "Compile Diagnostic Telemetry", "Synthesizes AI vision diagnosis and hardware sensor anomalies.", false, "COMPLETED"));
                steps.add(createStep(planId, 2, "FIND_SHOPS", "Match Top-Rated Certified Shops", "Ranks certified repair shops by proximity, trust score, and category capability.", false, "PENDING"));
                steps.add(createStep(planId, 3, "COMPARE_OPTIONS", "Analyze Price Fairness & Warranty", "Benchmarks quoted component replacement costs against regional market averages.", false, "PENDING"));
                steps.add(createStep(planId, 4, "REQUEST_QUOTE", "Dispatch Formal Quotation Request", "Sends itemized diagnostic report to top selected repair shop.", true, "WAITING_APPROVAL"));
                steps.add(createStep(planId, 5, "BOOK_SERVICE", "Confirm & Schedule Repair Booking", "Authorizes certified repair service and activates repair warranty guarantee.", true, "PENDING"));
            }
            case "MAINTENANCE" -> {
                steps.add(createStep(planId, 1, "GENERATE_REPORT", "Review Maintenance Checklist", "Identifies overdue thermal paste, port cleaning, and battery calibration items.", false, "COMPLETED"));
                steps.add(createStep(planId, 2, "SCHEDULE_MAINTENANCE", "Schedule Maintenance Window", "Creates calendar reminder and automated device inspection task.", true, "WAITING_APPROVAL"));
                steps.add(createStep(planId, 3, "NOTIFY_USER", "Send Care Guide & Instructions", "Delivers step-by-step preventative maintenance walkthrough to user.", false, "PENDING"));
            }
            case "PROFESSIONAL_SERVICE" -> {
                steps.add(createStep(planId, 1, "GENERATE_REPORT", "Flag Critical Safety Telemetry", "Documents high thermal or circuit hazard risks for technician review.", false, "COMPLETED"));
                steps.add(createStep(planId, 2, "FIND_SHOPS", "Locate Authorized Service Center", "Identifies manufacturer-authorized service partner with OEM testing equipment.", false, "PENDING"));
                steps.add(createStep(planId, 3, "BOOK_SERVICE", "Request Certified Inspection", "Books professional diagnostic triage appointment.", true, "WAITING_APPROVAL"));
            }
            case "REFURBISH" -> {
                steps.add(createStep(planId, 1, "GENERATE_REPORT", "Evaluate Refurbish ROI", "Calculates cost vs lifespan extension potential for battery and storage modules.", false, "COMPLETED"));
                steps.add(createStep(planId, 2, "COMPARE_OPTIONS", "Source Upgrade Components", "Compares genuine OEM upgrade modules and warranty tiers.", false, "PENDING"));
                steps.add(createStep(planId, 3, "BOOK_SERVICE", "Authorize Refurbishment Service", "Confirms overhaul booking with certified refurbishment center.", true, "WAITING_APPROVAL"));
            }
            case "RECYCLE" -> {
                steps.add(createStep(planId, 1, "GENERATE_REPORT", "Certify End-of-Life Status", "Verifies that repair cost exceeds residual value and hardware is non-viable.", false, "COMPLETED"));
                steps.add(createStep(planId, 2, "DISPOSE_RECYCLE", "Locate Certified E-Waste Drop-off", "Maps nearest certified R2/e-Stewards recycling facility.", false, "PENDING"));
                steps.add(createStep(planId, 3, "NOTIFY_USER", "Confirm Circular Disposal", "Records avoided landfill metric and archives device digital passport.", true, "WAITING_APPROVAL"));
            }
            default -> { // MONITOR / DEVICE_OPTIMIZATION
                steps.add(createStep(planId, 1, "GENERATE_REPORT", "Establish Baseline Telemetry", "Records current sensor operating temperatures and battery cycle health.", false, "COMPLETED"));
                steps.add(createStep(planId, 2, "NOTIFY_USER", "Activate Automated Health Watch", "Schedules background weekly telemetry verification checks.", false, "PENDING"));
            }
        }

        return steps;
    }

    private AutonomousActionStep createStep(
            String planId,
            int order,
            String actionType,
            String title,
            String description,
            boolean requiresApproval,
            String initialStatus
    ) {
        return AutonomousActionStep.builder()
                .planId(planId)
                .stepOrder(order)
                .actionType(actionType)
                .title(title)
                .description(description)
                .requiresApproval(requiresApproval)
                .status(initialStatus)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .completedAt("COMPLETED".equals(initialStatus) ? LocalDateTime.now() : null)
                .build();
    }
}
