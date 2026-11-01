package com.university.club.membership.dto;

import com.university.club.membership.entity.MembershipStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record MembershipResponse(
        UUID id,
        UUID userId,
        String firstName,
        String lastName,
        String email,
        MembershipStatus status,
        Set<String> roles,
        LocalDateTime joinedAt
) {}