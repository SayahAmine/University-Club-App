package com.university.club.events.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CreateEventRequest(

        @NotBlank
        @Size(max = 200)
        String title,

        @Size(max = 10000)
        String description,

        @Size(max = 300)
        String location,

        @NotNull
        @Future
        LocalDateTime startAt,

        @NotNull
        LocalDateTime endAt,

        LocalDateTime registrationDeadline,

        @Positive
        Integer capacity,

        boolean requiresApproval

) {
}