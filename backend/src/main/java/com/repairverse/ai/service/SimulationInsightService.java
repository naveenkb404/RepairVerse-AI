package com.repairverse.ai.service;

import com.repairverse.ai.dto.DigitalTwinDto.*;
import com.repairverse.ai.entity.DigitalTwinOptimizationResult;
import com.repairverse.ai.entity.DigitalTwinSnapshot;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimulationInsightService {

    public List<SimulationInsight> generateInsights(
            DigitalTwinSnapshot snapshot,
            List<ScenarioResponse> scenarios,
            DigitalTwinOptimizationResult optimization,
            List<ForecastResponse> forecasts
    ) {
        List<SimulationInsight> insights = new ArrayList<>();

        if (snapshot == null) {
            return insights;
        }

        // 1. Reliability Insight
        int risk = snapshot.getFailureRiskScore() != null ? snapshot.getFailureRiskScore() : 35;
        int health = snapshot.getHealthScore() != null ? snapshot.getHealthScore() : 75;
        if (risk > 50) {
            insights.add(new SimulationInsight(
                    "RELIABILITY",
                    "Elevated Failure Risk",
                    String.format("Repairing within the next 30 days is projected to reduce failure risk by %d%% and restore health to %d%%.",
                            Math.min(45, (int)(risk * 0.6)), Math.min(95, health + 25)),
                    "RELIABILITY",
                    "HIGH"
            ));
        } else {
            insights.add(new SimulationInsight(
                    "RELIABILITY",
                    "Stable Operating Condition",
                    "Current operational health remains stable; proactive diagnostics prevent unexpected sudden component failures.",
                    "RELIABILITY",
                    "LOW"
            ));
        }

        // 2. Financial Insight
        double repairCost = snapshot.getPredictedRepairCost() != null ? snapshot.getPredictedRepairCost() : 3500.0;
        double delayCostPenalty = Math.round(repairCost * 0.45);
        insights.add(new SimulationInsight(
                "FINANCIAL",
                "Delay Escalation Warning",
                String.format("Delaying recommended repair past 90 days may increase the estimated repair cost by ₹%,.0f due to secondary component degradation.",
                        delayCostPenalty),
                "FINANCIAL",
                delayCostPenalty > 2000 ? "HIGH" : "MEDIUM"
        ));

        // 3. Strategy / Longevity Insight
        if (optimization != null && optimization.getRecommendedStrategy() != null) {
            String strat = optimization.getRecommendedStrategy();
            int lifespanGain = optimization.getEstimatedLifespanGain() != null ? optimization.getEstimatedLifespanGain() : 12;
            double savings = optimization.getEstimatedSavings() != null ? optimization.getEstimatedSavings() : 2500.0;

            String strategyMsg;
            if ("PREVENTIVE_MAINTENANCE".equalsIgnoreCase(strat)) {
                strategyMsg = String.format("Preventive maintenance provides the highest long-term value, extending usable lifecycle by %d months with estimated savings of ₹%,.0f.",
                        lifespanGain, savings);
            } else if ("REFURBISH_DEVICE".equalsIgnoreCase(strat)) {
                strategyMsg = String.format("Refurbishment is projected to extend usable lifecycle by %d months while preserving 80%% of original device value.",
                        lifespanGain);
            } else if ("REPAIR_NOW".equalsIgnoreCase(strat)) {
                strategyMsg = String.format("Immediate repair produces the optimal ROI, mitigating cascading faults and saving up to ₹%,.0f over 24 months.",
                        savings);
            } else {
                strategyMsg = String.format("Executing '%s' yields an optimization score of %d/100 with expected lifespan extension of %d months.",
                        strat.replace("_", " "), optimization.getOptimizationScore() != null ? optimization.getOptimizationScore() : 80, lifespanGain);
            }

            insights.add(new SimulationInsight(
                    "STRATEGY",
                    "Optimal Lifecycle Strategy",
                    strategyMsg,
                    "LONGEVITY",
                    "HIGH"
            ));
        }

        // 4. Sustainability Insight
        double co2Savings = (optimization != null && optimization.getEstimatedCo2Savings() != null)
                ? optimization.getEstimatedCo2Savings() : 18.5;
        insights.add(new SimulationInsight(
                "SUSTAINABILITY",
                "Circular Value Optimization",
                String.format("Choosing repair or refurbishment over replacement prevents approximately %.1f kg CO₂ and eliminates e-waste disposal.",
                        co2Savings),
                "SUSTAINABILITY",
                "MEDIUM"
        ));

        // 5. Risk Trajectory Insight
        if (forecasts != null && !forecasts.isEmpty()) {
            ForecastResponse f24 = forecasts.stream()
                    .filter(f -> f.forecastHorizonMonths() == 24)
                    .findFirst()
                    .orElse(forecasts.get(forecasts.size() - 1));

            insights.add(new SimulationInsight(
                    "TRAJECTORY",
                    "24-Month Trajectory Projection",
                    String.format("Without intervention, failure risk rises to %d%% by month %d with estimated device residual value falling to ₹%,.0f.",
                            f24.predictedFailureRisk(), f24.forecastHorizonMonths(), f24.predictedDeviceValue()),
                    "RISK",
                    f24.predictedFailureRisk() > 60 ? "HIGH" : "MEDIUM"
            ));
        }

        return insights;
    }
}
