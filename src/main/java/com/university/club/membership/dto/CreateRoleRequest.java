package com.university.club.membership.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(

        @NotBlank
        @Size(min = 3, max = 100)
        @Pattern(
                regexp = "^[A-Za-z][A-Za-z0-9_]*$",
                message = "Role name must contain only letters, numbers and underscores"
        )
        String name,

        @Size(max = 1000)
        String description

) {}