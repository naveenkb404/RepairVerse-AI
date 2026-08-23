package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairCostDto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairCostEstimatorService {

    public CostEstimateResponse calculateEstimate(CostEstimateRequest request) {
        String category = request.category() != null ? request.category() : "Smartphone";
        String model = request.deviceModel() != null ? request.deviceModel() : "Generic Device";
        String issue = request.issueType() != null ? request.issueType() : "Screen Damage";

        double basePartCost;
        double baseLaborHours;
        double replacementValue;
        List<String> suggestedParts;

        switch (category.toLowerCase()) {
            case "laptop":
                replacementValue = 1400.0;
                if (issue.toLowerCase().contains("battery")) {
                    basePartCost = 110.0;
                    baseLaborHours = 1.5;
                    suggestedParts = List.of("OEM 99.6Wh Battery Pack", "Thermal Paste", "Adhesive Pull Tabs");
                } else if (issue.toLowerCase().contains("screen") || issue.toLowerCase().contains("display")) {
                    basePartCost = 280.0;
                    baseLaborHours = 2.0;
                    suggestedParts = List.of("Liquid Retina Panel Assembly", "Hinge Bracket Set");
                } else {
                    basePartCost = 180.0;
                    baseLaborHours = 2.5;
                    suggestedParts = List.of("Logic Board Component Kit", "Power IC Chip");
                }
                break;
            case "tablet":
                replacementValue = 750.0;
                basePartCost = 120.0;
                baseLaborHours = 1.5;
                suggestedParts = List.of("Digitizer Front Glass", "OCA Optically Clear Adhesive", "Frame Seal");
                break;
            case "console":
                replacementValue = 500.0;
                basePartCost = 65.0;
                baseLaborHours = 1.0;
                suggestedParts = List.of("Ultra HD Blu-ray Laser Assembly", "Thermal Pad Set", "High-Flow Fan");
                break;
            case "audio":
                replacementValue = 250.0;
                basePartCost = 35.0;
                baseLaborHours = 0.8;
                suggestedParts = List.of("Driver Transducer Unit", "Headband Cushion Assembly");
                break;
            default: // Smartphone
                replacementValue = 900.0;
                if (issue.toLowerCase().contains("battery")) {
                    basePartCost = 45.0;
                    baseLaborHours = 0.8;
                    suggestedParts = List.of("OEM 3200mAh Li-ion Battery", "Pre-cut Waterproof Adhesive Gasket");
                } else {
                    basePartCost = 135.0;
                    baseLaborHours = 1.0;
                    suggestedParts = List.of("Super Retina XDR OLED Assembly", "Ear Speaker Mesh", "Perimeter Seal");
                }
                break;
        }

        // Option 1: DIY Channel (Parts cost + $15 tools, $0 labor)
        double diyParts = basePartCost + 15.0;
        double diyTotal = diyParts;
        CostOption diyOption = new CostOption(
                "DIY Repair",
                "Self-repair using precision tool kit and step-by-step interactive guide",
                diyParts,
                0.0,
                diyTotal,
                "1 - 2 hours",
                "Part Supplier Warranty (90 Days)",
                "Best Value"
        );

        // Option 2: Local Certified Technician (Parts + $45/hr labor)
        double localLabor = Math.round(baseLaborHours * 45.0);
        double localTotal = basePartCost + localLabor;
        CostOption localTechOption = new CostOption(
                "Local Certified Shop",
                "Professional repair by background-checked local repair technicians",
                basePartCost,
                localLabor,
                localTotal,
                "2 - 4 hours (Same Day)",
                "6 Months Parts & Labor Warranty",
                "Most Popular"
        );

        // Option 3: Authorized Brand Service (OEM parts markup + $95/hr labor)
        double authParts = Math.round(basePartCost * 1.5);
        double authLabor = Math.round(baseLaborHours * 95.0);
        double authTotal = authParts + authLabor;
        CostOption authServiceOption = new CostOption(
                "Authorized Service Center",
                "Official brand repair centre with manufacturer service guarantee",
                authParts,
                authLabor,
                authTotal,
                "3 - 7 Business Days",
                "1 Year Manufacturer Warranty",
                "Official Guarantee"
        );

        double maxSavingsDollars = Math.max(0, replacementValue - diyTotal);
        double maxSavingsPercent = Math.round((maxSavingsDollars / replacementValue) * 100.0);

        String recommendation = String.format(
                "Repairing is highly economical. You save up to $%.0f (%.0f%%) compared to purchasing a new replacement.",
                maxSavingsDollars, maxSavingsPercent
        );

        return new CostEstimateResponse(
                category,
                model,
                issue,
                replacementValue,
                diyOption,
                localTechOption,
                authServiceOption,
                maxSavingsDollars,
                maxSavingsPercent,
                recommendation,
                suggestedParts
        );
    }

    public List<CategoryIssueBaseline> getSupportedCategories() {
        return List.of(
                new CategoryIssueBaseline("Smartphone", List.of("Cracked Screen / OLED Display", "Degraded Battery", "Charging Port Damage", "Camera Lens Crack", "Back Glass Fracture")),
                new CategoryIssueBaseline("Laptop", List.of("Battery Capacity Degradation", "Broken Display Panel", "Keyboard / Keycap Malfunction", "Thermal Overheating / Fan Noise", "Trackpad Unresponsive")),
                new CategoryIssueBaseline("Tablet", List.of("Front Digitizer Glass Shatter", "Battery Drain", "Volume / Power Button Jam", "LCD Display Artifacts")),
                new CategoryIssueBaseline("Console", List.of("Optical Disc Read Error", "HDMI Port Physical Damage", "Overheating & Thermal Shutoff", "Internal Power Supply Failure")),
                new CategoryIssueBaseline("Audio", List.of("Distorted Speaker Driver", "Battery Depleted", "Broken Headband Hinge", "Lightning / USB-C Connector Loss"))
        );
    }
}
