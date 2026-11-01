package com.university.club.inventory.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateBorrowRequest(

        @NotNull
        UUID assetId,

        @Size(max = 3000)
        String purpose,

        @NotNull
        @Future
        LocalDateTime requestedFrom,

        @NotNull
        @Future
        LocalDateTime requestedUntil
) {}