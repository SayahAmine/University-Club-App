package com.university.club.events.dto;

import jakarta.validation.constraints.Size;

public record RegisterForEventRequest(

        @Size(max = 2000)
        String notes

) {
}