package com.university.club.membership.controller;

import com.university.club.membership.dto.MemberDetailsResponse;
import com.university.club.membership.entity.MembershipStatus;
import com.university.club.membership.service.MemberManagementService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
public class MemberManagementController {

    private final MemberManagementService memberManagementService;

    public MemberManagementController(
            MemberManagementService memberManagementService) {
        this.memberManagementService = memberManagementService;
    }

    // List all members
    @GetMapping
    @PreAuthorize("hasAuthority('MEMBER_VIEW')")
    public ResponseEntity<List<MemberDetailsResponse>> getAllMembers() {

        return ResponseEntity.ok(
                memberManagementService.getAllMembers()
        );
    }

    // Filter members by status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('MEMBER_VIEW')")
    public ResponseEntity<List<MemberDetailsResponse>> getMembersByStatus(
            @PathVariable MembershipStatus status) {

        return ResponseEntity.ok(
                memberManagementService.getMembersByStatus(status)
        );
    }

    // View one member
    @GetMapping("/{membershipId}")
    @PreAuthorize("hasAuthority('MEMBER_VIEW')")
    public ResponseEntity<MemberDetailsResponse> getMember(
            @PathVariable UUID membershipId) {

        return ResponseEntity.ok(
                memberManagementService.getMember(membershipId)
        );
    }

    // Suspend a membership
    @PatchMapping("/{membershipId}/suspend")
    @PreAuthorize("hasAuthority('MEMBER_SUSPEND')")
    public ResponseEntity<MemberDetailsResponse> suspendMembership(
            @PathVariable UUID membershipId) {

        return ResponseEntity.ok(
                memberManagementService.suspendMembership(membershipId)
        );
    }

    // Reactivate a suspended membership
    @PatchMapping("/{membershipId}/reactivate")
    @PreAuthorize("hasAuthority('MEMBER_SUSPEND')")
    public ResponseEntity<MemberDetailsResponse> reactivateMembership(
            @PathVariable UUID membershipId) {

        return ResponseEntity.ok(
                memberManagementService.reactivateMembership(membershipId)
        );
    }
}