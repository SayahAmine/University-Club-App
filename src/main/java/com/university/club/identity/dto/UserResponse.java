package com.university.club.identity.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(

        UUID id,
        String firstName,
        String lastName,
        String email,
        String studentNumber,
        String accountStatus,
        LocalDateTime createdAt

) {}