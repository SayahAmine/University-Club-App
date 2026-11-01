package com.university.club.inventory.dto;

import jakarta.validation.constraints.Size;

public record BorrowReviewRequest(

        @Size(max = 2000)
        String notes
) {}