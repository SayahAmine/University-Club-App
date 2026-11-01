package com.university.club.identity.controller;

import com.university.club.common.security.UserPrincipal;
import com.university.club.identity.dto.UserResponse;
import com.university.club.identity.service.UserService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {

        return userService.getProfile(principal.getId());
    }
}