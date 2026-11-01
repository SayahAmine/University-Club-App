package com.university.club.membership.dto;

import java.util.Set;
import java.util.UUID;

public record RoleResponse(
        UUID id,
        String name,
        String description,
        boolean system,
        Set<String> permissions
) {}