package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairHistoryDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.RepairHistory;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.RepairHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairHistoryServiceTest {

    @Mock
    private RepairHistoryRepository repairHistoryRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private RepairHistoryService repairHistoryService;

    @Test
    @DisplayName("Should return sample repair history when user has no DB records")
    void getRepairHistory_SampleFallback() {
        when(repairHistoryRepository.findByUserIdOrderByRepairDateDesc("usr-1")).thenReturn(List.of());

        List<RepairHistoryItemResponse> list = repairHistoryService.getRepairHistoryForUser("usr-1");

        assertThat(list).isNotEmpty();
        assertThat(list.get(0).id()).isEqualTo("rep_sample_101");
    }

    @Test
    @DisplayName("Should return user repair history when DB records exist")
    void getRepairHistory_LiveRecords() {
        RepairHistory rep = RepairHistory.builder()
                .id("rep-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .repairType("Screen Fix")
                .repairDate("2026-08-01")
                .status("Completed")
                .partsCost(50.0)
                .laborCost(30.0)
                .totalCost(80.0)
                .createdAt(LocalDateTime.now())
                .build();

        Device dev = Device.builder().id("dev-1").deviceName("iPhone").build();

        when(repairHistoryRepository.findByUserIdOrderByRepairDateDesc("usr-1")).thenReturn(List.of(rep));
        when(deviceRepository.findById("dev-1")).thenReturn(Optional.of(dev));

        List<RepairHistoryItemResponse> list = repairHistoryService.getRepairHistoryForUser("usr-1");

        assertThat(list).hasSize(1);
        assertThat(list.get(0).id()).isEqualTo("rep-1");
        assertThat(list.get(0).repairType()).isEqualTo("Screen Fix");
    }

    @Test
    @DisplayName("Should find repair history by id or throw ResourceNotFoundException")
    void getRepairHistoryById_NotFound() {
        when(repairHistoryRepository.findByIdAndUserId("rep-999", "usr-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repairHistoryService.getRepairHistoryById("usr-1", "rep-999"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create repair record successfully")
    void createRepairRecord_Success() {
        Device dev = Device.builder().id("dev-1").deviceName("iPhone").build();
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-1")).thenReturn(Optional.of(dev));

        RepairHistory rep = RepairHistory.builder()
                .id("rep-new")
                .userId("usr-1")
                .deviceId("dev-1")
                .repairType("Battery Replacement")
                .repairDate("2026-08-20")
                .status("Completed")
                .partsCost(40.0)
                .laborCost(20.0)
                .totalCost(60.0)
                .build();

        when(repairHistoryRepository.save(any(RepairHistory.class))).thenReturn(rep);
        when(deviceRepository.findById("dev-1")).thenReturn(Optional.of(dev));

        CreateRepairHistoryRequest request = new CreateRepairHistoryRequest(
                "dev-1", "Battery Replacement", "2026-08-20", "Completed",
                "Replaced degraded battery", "Battery Issue", 95,
                "Tech A", "Specialist", "Shop A", "123 St",
                List.of(), 40.0, 20.0, 60.0, "1 hour",
                "6 Months", "2027-02-20", true, 10.0, 0.1, 80.0,
                "Notes", List.of()
        );

        RepairHistoryItemResponse created = repairHistoryService.createRepairRecord("usr-1", request);

        assertThat(created.id()).isEqualTo("rep-new");
        assertThat(created.repairType()).isEqualTo("Battery Replacement");
    }
}
