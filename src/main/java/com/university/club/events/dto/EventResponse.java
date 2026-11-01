package com.university.club.events.dto;

import com.university.club.events.entity.EventStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventResponse(

        UUID id,

        String title,

        String description,

        String location,

        LocalDateTime startAt,

        LocalDateTime endAt,

        LocalDateTime registrationDeadline,

        Integer capacity,

        boolean requiresApproval,

        EventStatus status,

        UUID createdById,

        String createdByName,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}