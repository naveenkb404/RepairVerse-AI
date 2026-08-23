package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairCostDto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RepairCostEstimatorServiceTest {

    @InjectMocks
    private RepairCostEstimatorService repairCostEstimatorService;

    @Test
    @DisplayName("Should calculate cost estimate with DIY, Local, and Authorized channels")
    void calculateEstimate_Smartphone() {
        CostEstimateRequest req = new CostEstimateRequest("Smartphone", "iPhone 13", "Cracked Screen", "1");
        CostEstimateResponse res = repairCostEstimatorService.calculateEstimate(req);

        assertThat(res.category()).isEqualTo("Smartphone");
        assertThat(res.diyOption()).isNotNull();
        assertThat(res.localTechOption()).isNotNull();
        assertThat(res.authorizedServiceOption()).isNotNull();
        assertThat(res.diyOption().totalCost()).isLessThan(res.authorizedServiceOption().totalCost());
        assertThat(res.maxSavingsDollars()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should return supported hardware categories")
    void getSupportedCategories_Success() {
        List<CategoryIssueBaseline> list = repairCostEstimatorService.getSupportedCategories();

        assertThat(list).isNotEmpty();
        assertThat(list.stream().anyMatch(c -> c.category().equals("Smartphone"))).isTrue();
    }
}
