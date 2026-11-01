package com.university.club.inventory.dto;

import com.university.club.inventory.entity.AssetCondition;
import com.university.club.inventory.entity.AssetStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AssetResponse(
        UUID id,
        String name,
        String description,
        String serialNumber,
        String inventoryCode,
        UUID categoryId,
        String categoryName,
        AssetCondition condition,
        AssetStatus status,
        LocalDate purchaseDate,
        BigDecimal purchasePrice,
        String location,
        UUID createdById,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}