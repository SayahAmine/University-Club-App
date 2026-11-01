package com.university.club.inventory.dto;

import com.university.club.inventory.entity.BorrowStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record BorrowResponse(

        UUID id,

        UUID assetId,
        String assetName,
        String inventoryCode,

        UUID userId,
        String firstName,
        String lastName,
        String email,

        String purpose,

        LocalDateTime requestedFrom,
        LocalDateTime requestedUntil,

        BorrowStatus status,

        UUID reviewedBy,
        LocalDateTime reviewedAt,

        LocalDateTime borrowedAt,
        LocalDateTime returnedAt,

        String notes,
        LocalDateTime createdAt
) {}