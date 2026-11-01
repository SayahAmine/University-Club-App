package com.university.club.inventory.dto;

import com.university.club.inventory.entity.AssetCondition;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateAssetRequest(

        @NotBlank
        @Size(max = 200)
        String name,

        @Size(max = 10000)
        String description,

        @Size(max = 150)
        String serialNumber,

        @NotNull
        UUID categoryId,

        @NotNull
        AssetCondition condition,

        LocalDate purchaseDate,

        @DecimalMin("0.00")
        BigDecimal purchasePrice,

        @Size(max = 200)
        String location
) {}