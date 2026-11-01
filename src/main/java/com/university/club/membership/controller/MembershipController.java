package com.university.club.membership.controller;

import com.university.club.common.security.UserPrincipal;
import com.university.club.membership.dto.MembershipResponse;
import com.university.club.membership.service.MembershipService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/memberships")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/me")
    public ResponseEntity<MembershipResponse> getMyMembership(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                membershipService.getMyMembership(principal.getId())
        );
    }
}