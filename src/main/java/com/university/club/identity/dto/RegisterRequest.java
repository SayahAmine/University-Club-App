package com.university.club.identity.dto;


import jakarta.validation.constraints.*;

public record RegisterRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100)
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 10, max = 72,
                message = "Password must contain between 10 and 72 characters")
        String password,

        @NotBlank(message = "Password confirmation is required")
        String confirmPassword,

        @Size(max = 50)
        String studentNumber

) {}