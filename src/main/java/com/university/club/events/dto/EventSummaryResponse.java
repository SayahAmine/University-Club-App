package com.university.club.events.dto;

import com.university.club.events.entity.EventStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventSummaryResponse(
        UUID id,
        String title,
        String location,
        LocalDateTime startAt,
        LocalDateTime endAt,
        EventStatus status,
        Integer capacity,
        long approvedRegistrations,
        long pendingRegistrations,
        Long remainingCapacity,
        boolean requiresApproval
) {}