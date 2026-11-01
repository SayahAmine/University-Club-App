package com.university.club.events.controller;

import com.university.club.events.dto.CreateEventRequest;
import com.university.club.events.dto.EventResponse;
import com.university.club.events.dto.UpdateEventRequest;

import com.university.club.common.security.UserPrincipal;

import com.university.club.events.service.EventService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @GetMapping("/public")
    public List<EventResponse> getPublishedEvents() {

        return eventService.getPublishedEvents();
    }

    @GetMapping("/public/{eventId}")
    public EventResponse getPublicEvent(
            @PathVariable UUID eventId
    ) {

        EventResponse event = eventService.getEvent(eventId);

        if (event.status() !=
                com.university.club.events.entity.EventStatus.PUBLISHED) {
            throw new com.university.club.events.exception.InvalidEventStateException(
                    "This event is not publicly available."
            );
        }

        return event;
    }



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('EVENT_CREATE')")
    public EventResponse createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        return eventService.createEvent(
                request,
                principal.getId()
        );
    }



    @GetMapping
    @PreAuthorize("hasAuthority('EVENT_VIEW')")
    public List<EventResponse> getAllEvents() {

        return eventService.getAllEvents();
    }

    @GetMapping("/{eventId}")
    @PreAuthorize("hasAuthority('EVENT_VIEW')")
    public EventResponse getEvent(
            @PathVariable UUID eventId
    ) {

        return eventService.getEvent(eventId);
    }


    @PutMapping("/{eventId}")
    @PreAuthorize("hasAuthority('EVENT_UPDATE')")
    public EventResponse updateEvent(
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventRequest request
    ) {

        return eventService.updateEvent(eventId, request);
    }

    @PatchMapping("/{eventId}/publish")
    @PreAuthorize("hasAuthority('EVENT_CREATE')")
    public EventResponse publishEvent(
            @PathVariable UUID eventId
    ) {

        return eventService.publishEvent(eventId);
    }



    @PatchMapping("/{eventId}/cancel")
    @PreAuthorize("hasAuthority('EVENT_DELETE')")
    public EventResponse cancelEvent(
            @PathVariable UUID eventId
    ) {

        return eventService.cancelEvent(eventId);
    }


    @PatchMapping("/{eventId}/complete")
    @PreAuthorize("hasAuthority('EVENT_UPDATE')")
    public EventResponse completeEvent(
            @PathVariable UUID eventId
    ) {

        return eventService.completeEvent(eventId);
    }
}