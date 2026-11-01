package com.university.club.membership.dto;

import com.university.club.membership.entity.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationResponse(
        UUID id,
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String studentNumber,
        String motivation,
        ApplicationStatus status,
        String reviewedBy,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt
) {}