package com.university.club.events.dto;

import com.university.club.events.entity.AttendanceStatus;

import jakarta.validation.constraints.NotNull;

public record AttendanceRequest(

        @NotNull
        AttendanceStatus status

) {
}