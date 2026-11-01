package com.university.club.inventory.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateAssetRequest(

        @NotBlank
        @Size(max = 200)
        String name,

        @Size(max = 10000)
        String description,

        @Size(max = 150)
        String serialNumber,

        @NotBlank
        @Size(max = 100)
        String inventoryCode,

        @NotNull
        UUID categoryId,

        LocalDate purchaseDate,

        @DecimalMin("0.00")
        BigDecimal purchasePrice,

        @Size(max = 200)
        String location
) {}