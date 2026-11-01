package com.university.club.membership.controller;

import com.university.club.common.security.UserPrincipal;
import com.university.club.membership.dto.ApplicationRequest;
import com.university.club.membership.dto.ApplicationResponse;
import com.university.club.membership.service.MembershipApplicationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/membership-applications")
public class MembershipApplicationController {

    private final MembershipApplicationService applicationService;

    public MembershipApplicationController(
            MembershipApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // A logged-in user submits an application
    @PostMapping
    public ResponseEntity<ApplicationResponse> submitApplication(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ApplicationRequest request) {

        ApplicationResponse response =
                applicationService.submitApplication(
                        principal.getId(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // A user views their own application history
    @GetMapping("/me")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                applicationService.getMyApplications(principal.getId())
        );
    }

    // Authorized members view pending applications
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('MEMBER_VIEW')")
    public ResponseEntity<List<ApplicationResponse>> getPendingApplications() {

        return ResponseEntity.ok(
                applicationService.getPendingApplications()
        );
    }

    // Authorized members approve an application
    @PatchMapping("/{applicationId}/approve")
    @PreAuthorize("hasAuthority('MEMBER_APPROVE')")
    public ResponseEntity<ApplicationResponse> approveApplication(
            @PathVariable UUID applicationId,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                applicationService.approveApplication(
                        applicationId,
                        principal.getId()
                )
        );
    }

    // Authorized members reject an application
    @PatchMapping("/{applicationId}/reject")
    @PreAuthorize("hasAuthority('MEMBER_REJECT')")
    public ResponseEntity<ApplicationResponse> rejectApplication(
            @PathVariable UUID applicationId,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                applicationService.rejectApplication(
                        applicationId,
                        principal.getId()
                )
        );
    }
}