package com.university.club.membership.dto;

import com.university.club.membership.entity.MembershipStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record MemberDetailsResponse(
        UUID membershipId,
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String studentNumber,
        MembershipStatus status,
        Set<String> roles,
        LocalDateTime joinedAt
) {
}