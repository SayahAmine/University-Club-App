package com.university.club.membership.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApplicationRequest(

        @NotBlank(message = "Motivation is required")
        @Size(max = 3000, message = "Motivation cannot exceed 3000 characters")
        String motivation

) {}