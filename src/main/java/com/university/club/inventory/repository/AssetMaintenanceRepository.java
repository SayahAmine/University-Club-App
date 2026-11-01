package com.university.club.inventory.repository;

import com.university.club.inventory.entity.AssetMaintenance;
import com.university.club.inventory.entity.MaintenanceStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssetMaintenanceRepository
        extends JpaRepository<AssetMaintenance, UUID> {

    @EntityGraph(attributePaths = {"asset", "recordedBy", "performedBy"})
    List<AssetMaintenance> findAllByAsset_IdOrderByStartedAtDesc(
            UUID assetId
    );

    @EntityGraph(attributePaths = {"asset", "recordedBy", "performedBy"})
    List<AssetMaintenance> findAllByOrderByStartedAtDesc();

    long countByStatus(MaintenanceStatus status);
}