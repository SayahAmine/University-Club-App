package com.university.club.inventory.dto;

import com.university.club.inventory.entity.AssetCondition;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReturnAssetRequest(

        @NotNull
        AssetCondition condition,

        @Size(max = 2000)
        String notes
) {}