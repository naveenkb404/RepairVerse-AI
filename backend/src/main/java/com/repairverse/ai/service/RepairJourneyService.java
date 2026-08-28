package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairPlanningDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairJourneyService {

    private final DeviceRepository deviceRepository;
    private final DiagnosisReportRepository diagnosisReportRepository;
    private final DevicePredictionRepository devicePredictionRepository;
    private final AIRecommendationRepository recommendationRepository;
    private final RepairActionPlanRepository actionPlanRepository;
    private final BookingRepository bookingRepository;
    private final RepairHistoryRepository repairHistoryRepository;
    private final MaintenanceScheduleRepository maintenanceScheduleRepository;

    /**
     * Determines current position in the unified 9-stage repair journey pipeline.
     */
    @Transactional(readOnly = true)
    public RepairJourneyResponse getRepairJourney(String deviceId, String userId) {
        Device device = validateDeviceOwnership(deviceId, userId);

        Optional<DiagnosisReport> diagOpt = diagnosisReportRepository.findTopByDeviceIdOrderByCreatedAtDesc(deviceId);
        Optional<DevicePrediction> predOpt = devicePredictionRepository.findByDeviceId(deviceId);
        Optional<AIRecommendation> recOpt = diagOpt.flatMap(d -> recommendationRepository.findByDiagnosisId(d.getId()));
        Optional<RepairActionPlan> planOpt = actionPlanRepository.findFirstByDeviceIdAndUserIdOrderByCreatedAtDesc(deviceId, userId);
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<RepairHistory> history = repairHistoryRepository.findByDeviceIdOrderByRepairDateDesc(deviceId);
        List<MaintenanceSchedule> maintenance = maintenanceScheduleRepository.findByUserIdAndDeviceIdOrderByDueDateAsc(userId, deviceId);

        boolean hasDiagnosis = diagOpt.isPresent();
        boolean hasPrediction = predOpt.isPresent();
        boolean hasRecommendation = recOpt.isPresent();
        boolean hasPlan = planOpt.isPresent();
        boolean hasBooking = !bookings.isEmpty();
        boolean hasRepairHistory = !history.isEmpty();

        List<RepairJourneyStageResponse> stages = new ArrayList<>();

        // Stage 1: DEVICE_REGISTERED
        stages.add(new RepairJourneyStageResponse(
            "DEVICE_REGISTERED",
            "Device Registered",
            "Hardware enrolled in RepairVerse Digital Health Passport ledger.",
            true,
            !hasDiagnosis,
            device.getCreatedAt(),
            "/devices/" + deviceId
        ));

        // Stage 2: DIAGNOSIS_COMPLETE
        stages.add(new RepairJourneyStageResponse(
            "DIAGNOSIS_COMPLETE",
            "AI Visual Diagnosis",
            hasDiagnosis ? "Completed: " + diagOpt.get().getProbableIssue() : "Upload optical scan or detail symptoms for AI diagnosis.",
            hasDiagnosis,
            hasDiagnosis && !hasPrediction,
            diagOpt.map(DiagnosisReport::getCreatedAt).orElse(null),
            "/diagnosis?deviceId=" + deviceId
        ));

        // Stage 3: RISK_ANALYZED
        stages.add(new RepairJourneyStageResponse(
            "RISK_ANALYZED",
            "Predictive Degradation Analysis",
            hasPrediction ? "Risk evaluated as " + predOpt.get().getRiskLevel() + " (Score " + predOpt.get().getPredictionScore() + "/100)" : "Deterministic failure probability and component wear scored.",
            hasPrediction,
            hasPrediction && !hasRecommendation,
            predOpt.map(DevicePrediction::getEvaluatedAt).orElse(null),
            "/devices/" + deviceId
        ));

        // Stage 4: REPAIR_RECOMMENDED
        stages.add(new RepairJourneyStageResponse(
            "REPAIR_RECOMMENDED",
            "Repair vs Replace Decision",
            hasRecommendation ? "Recommended Action: " + recOpt.get().getAction() : "Economic and carbon ROI feasibility matrix computed.",
            hasRecommendation,
            hasRecommendation && !hasPlan,
            recOpt.map(AIRecommendation::getCreatedAt).orElse(null),
            hasDiagnosis ? "/recommendation?diagnosisId=" + diagOpt.get().getId() : "/recommendation"
        ));

        // Stage 5: ACTION_PLAN_READY
        stages.add(new RepairJourneyStageResponse(
            "ACTION_PLAN_READY",
            "Smart Action Plan Generated",
            hasPlan ? "Strategy: " + planOpt.get().getOverallStrategy() + " (" + planOpt.get().getSteps().size() + " ordered steps)" : "Autonomous step-by-step roadmap ready for execution.",
            hasPlan,
            hasPlan && !hasBooking,
            planOpt.map(RepairActionPlan::getCreatedAt).orElse(null),
            "/devices/" + deviceId
        ));

        // Stage 6: SHOP_BOOKED
        stages.add(new RepairJourneyStageResponse(
            "SHOP_BOOKED",
            "Certified Technician Booking",
            hasBooking ? "Appointment scheduled with eco-certified repair center." : "Connect with nearby verified repair shops.",
            hasBooking,
            hasBooking && !hasRepairHistory,
            hasBooking ? bookings.get(0).getCreatedAt() : null,
            "/repair-shops"
        ));

        // Stage 7: REPAIR_IN_PROGRESS
        boolean inProgress = hasBooking && "SCHEDULED".equalsIgnoreCase(bookings.get(0).getBookingStatus());
        stages.add(new RepairJourneyStageResponse(
            "REPAIR_IN_PROGRESS",
            "Hardware Servicing & Teardown",
            inProgress ? "Device currently undergoing precision component replacement." : "Hardware disassembly and precision replacement in progress.",
            hasRepairHistory,
            inProgress,
            null,
            "/dashboard"
        ));

        // Stage 8: REPAIR_COMPLETED
        stages.add(new RepairJourneyStageResponse(
            "REPAIR_COMPLETED",
            "Quality Assurance & Verification",
            hasRepairHistory ? "Repair verified and logged to immutable service history." : "Post-repair burn-in testing and verification completed.",
            hasRepairHistory,
            false,
            hasRepairHistory ? history.get(0).getCreatedAt() : null,
            "/repair-history"
        ));

        // Stage 9: DEVICE_MONITORED
        boolean hasMaintenance = !maintenance.isEmpty();
        String stage9Desc = hasMaintenance
            ? "Digital passport updated. Active proactive care schedules (" + maintenance.size() + " tasks configured)."
            : "Digital passport updated. Active continuous telemetry and circular warranty active.";

        stages.add(new RepairJourneyStageResponse(
            "DEVICE_MONITORED",
            "Extended Lifecycle Monitoring",
            stage9Desc,
            hasRepairHistory || hasMaintenance,
            hasRepairHistory || hasMaintenance,
            null,
            "/devices/" + deviceId
        ));

        // Calculate progress and determine current stage
        int completedCount = 0;
        int currentIndex = 0;
        for (int i = 0; i < stages.size(); i++) {
            if (stages.get(i).isCompleted()) {
                completedCount++;
                currentIndex = i;
            }
        }
        if (currentIndex < stages.size() - 1 && !stages.get(currentIndex + 1).isCompleted()) {
            currentIndex = Math.min(stages.size() - 1, currentIndex + 1);
        }

        String currentStageKey = stages.get(currentIndex).stageKey();
        int progress = (int) Math.round((completedCount / (double) stages.size()) * 100.0);
        String nextAction = deriveNextAction(currentStageKey, deviceId);

        return new RepairJourneyResponse(
            device.getId(),
            device.getDeviceName(),
            currentStageKey,
            currentIndex,
            stages.size(),
            progress,
            stages,
            nextAction,
            LocalDateTime.now()
        );
    }

    private String deriveNextAction(String stageKey, String deviceId) {
        return switch (stageKey) {
            case "DEVICE_REGISTERED" -> "Run an AI visual diagnosis to assess physical wear and screen/battery integrity.";
            case "DIAGNOSIS_COMPLETE" -> "Evaluate predictive failure probability and component degradation curves.";
            case "RISK_ANALYZED" -> "Generate actionable Repair vs Replace decision and savings comparison.";
            case "REPAIR_RECOMMENDED" -> "Review the autonomous Smart Action Plan and recommended execution steps.";
            case "ACTION_PLAN_READY" -> "Book a certified local repair shop or acquire recommended DIY toolkits.";
            case "SHOP_BOOKED" -> "Deliver or ship hardware to the selected repair facility.";
            case "REPAIR_IN_PROGRESS" -> "Awaiting technician quality assurance testing and final reassembly.";
            case "REPAIR_COMPLETED" -> "Update device digital health passport and log lifetime carbon savings.";
            default -> "Continuous telemetry monitoring active. Maintain regular health checks.";
        };
    }

    private Device validateDeviceOwnership(String deviceId, String userId) {
        return deviceRepository.findByIdAndUserId(deviceId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Device not found or not owned by user: " + deviceId));
    }
}
