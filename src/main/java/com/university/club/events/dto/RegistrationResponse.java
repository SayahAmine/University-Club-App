package com.university.club.events.dto;

import com.university.club.events.entity.AttendanceStatus;
import com.university.club.events.entity.RegistrationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegistrationResponse(

        UUID id,

        UUID eventId,

        String eventTitle,

        UUID userId,

        String firstName,

        String lastName,

        String email,

        RegistrationStatus status,

        AttendanceStatus attendanceStatus,

        LocalDateTime registeredAt,

        UUID reviewedBy,

        LocalDateTime reviewedAt,

        LocalDateTime attendanceMarkedAt,

        String notes

) {
}