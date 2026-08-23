package com.repairverse.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.RepairGuideDto.*;
import com.repairverse.ai.entity.RepairGuide;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.RepairGuideRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairGuideServiceTest {

    @Mock
    private RepairGuideRepository repairGuideRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private RepairGuideService repairGuideService;

    @Test
    @DisplayName("Should return sample guides when repository is empty")
    void getAllGuides_SampleFallback() {
        when(repairGuideRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        List<GuideSummaryResponse> list = repairGuideService.getAllGuides(null, null);

        assertThat(list).isNotEmpty();
        assertThat(list.get(0).id()).isEqualTo("guide-001");
    }

    @Test
    @DisplayName("Should return live guides when available in database")
    void getAllGuides_Live() {
        RepairGuide guide = RepairGuide.builder()
                .id("g-1")
                .title("Battery Replacement")
                .category("Smartphone")
                .difficulty("Beginner")
                .estimatedTime("30m")
                .createdAt(LocalDateTime.now())
                .build();

        when(repairGuideRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(guide));

        List<GuideSummaryResponse> list = repairGuideService.getAllGuides(null, null);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).title()).isEqualTo("Battery Replacement");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if guide not found in live or sample")
    void getGuideById_NotFound() {
        when(repairGuideRepository.findById("g-none")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repairGuideService.getGuideById("g-none"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create guide successfully")
    void createGuide_Success() {
        RepairGuide guide = RepairGuide.builder()
                .id("g-new")
                .title("New Screen Guide")
                .category("Smartphone")
                .difficulty("Intermediate")
                .build();

        when(repairGuideRepository.save(any(RepairGuide.class))).thenReturn(guide);

        CreateGuideRequest req = new CreateGuideRequest("New Screen Guide", "Smartphone", "Intermediate", "40m", "Content", List.of(), List.of());
        GuideDetailResponse res = repairGuideService.createGuide("usr-1", "Author", req);

        assertThat(res.id()).isEqualTo("g-new");
        assertThat(res.title()).isEqualTo("New Screen Guide");
    }
}
