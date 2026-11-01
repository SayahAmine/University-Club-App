package com.university.club.events.controller;

import com.university.club.common.security.UserPrincipal;
import com.university.club.events.dto.RegisterForEventRequest;
import com.university.club.events.dto.RegistrationResponse;
import com.university.club.events.service.EventRegistrationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class EventRegistrationController {

    private final EventRegistrationService registrationService;

    public EventRegistrationController(
            EventRegistrationService registrationService
    ) {
        this.registrationService = registrationService;
    }

    // Register for an event
    @PostMapping("/events/{eventId}/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('EVENT_REGISTER')")
    public RegistrationResponse register(
            @PathVariable UUID eventId,
            @Valid @RequestBody(required = false)
            RegisterForEventRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return registrationService.register(
                eventId,
                principal.getId(),
                request
        );
    }

    // Get my registrations
    @GetMapping("/event-registrations/me")
    @PreAuthorize("isAuthenticated()")
    public List<RegistrationResponse> getMyRegistrations(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return registrationService.getMyRegistrations(
                principal.getId()
        );
    }

    // Get all registrations for an event
    @GetMapping("/events/{eventId}/registrations")
    @PreAuthorize("hasAuthority('EVENT_VIEW')")
    public List<RegistrationResponse> getEventRegistrations(
            @PathVariable UUID eventId
    ) {
        return registrationService.getEventRegistrations(eventId);
    }

    // Get pending registrations
    @GetMapping("/events/{eventId}/registrations/pending")
    @PreAuthorize("hasAuthority('EVENT_APPROVE_REGISTRATION')")
    public List<RegistrationResponse> getPendingRegistrations(
            @PathVariable UUID eventId
    ) {
        return registrationService.getPendingRegistrations(eventId);
    }

    // Approve registration
    @PatchMapping("/event-registrations/{registrationId}/approve")
    @PreAuthorize("hasAuthority('EVENT_APPROVE_REGISTRATION')")
    public RegistrationResponse approve(
            @PathVariable UUID registrationId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return registrationService.approve(
                registrationId,
                principal.getId()
        );
    }

    // Reject registration
    @PatchMapping("/event-registrations/{registrationId}/reject")
    @PreAuthorize("hasAuthority('EVENT_APPROVE_REGISTRATION')")
    public RegistrationResponse reject(
            @PathVariable UUID registrationId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return registrationService.reject(
                registrationId,
                principal.getId()
        );
    }

    // Cancel my registration
    @PatchMapping("/event-registrations/{registrationId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public RegistrationResponse cancel(
            @PathVariable UUID registrationId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return registrationService.cancelMyRegistration(
                registrationId,
                principal.getId()
        );
    }
}