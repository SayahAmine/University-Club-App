package com.university.club.inventory.dto;

import com.university.club.inventory.entity.MaintenanceStatus;
import com.university.club.inventory.entity.MaintenanceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MaintenanceResponse(
        UUID id,
        UUID assetId,
        String assetName,
        String inventoryCode,
        String title,
        String description,
        MaintenanceType type,
        BigDecimal cost,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        UUID performedById,
        UUID recordedById,
        MaintenanceStatus status
) {}