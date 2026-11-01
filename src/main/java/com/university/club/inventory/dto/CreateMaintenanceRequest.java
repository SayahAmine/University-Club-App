package com.university.club.inventory.dto;

import com.university.club.inventory.entity.MaintenanceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateMaintenanceRequest(

        @NotNull
        UUID assetId,

        @NotBlank
        @Size(max = 200)
        String title,

        @Size(max = 5000)
        String description,

        @NotNull
        MaintenanceType type,

        @DecimalMin("0.00")
        BigDecimal cost
) {}