package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairShopSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairShopSpecializationRepository extends JpaRepository<RepairShopSpecialization, String> {

    List<RepairShopSpecialization> findByRepairShopId(String repairShopId);

    List<RepairShopSpecialization> findByDeviceCategoryIgnoreCase(String deviceCategory);

    List<RepairShopSpecialization> findByBrandIgnoreCase(String brand);

    List<RepairShopSpecialization> findByDeviceCategoryIgnoreCaseAndBrandIgnoreCase(String deviceCategory, String brand);

    boolean existsByRepairShopIdAndDeviceCategoryIgnoreCaseAndBrandIgnoreCase(
            String repairShopId, String deviceCategory, String brand);
}
