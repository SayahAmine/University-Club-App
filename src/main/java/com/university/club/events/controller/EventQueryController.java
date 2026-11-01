package com.university.club.events.controller;

import com.university.club.events.dto.EventDashboardResponse;
import com.university.club.events.dto.EventResponse;
import com.university.club.events.dto.EventSummaryResponse;
import com.university.club.events.service.EventDashboardService;
import com.university.club.events.service.EventQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventQueryController {

    private final EventDashboardService dashboardService;
    private final EventQueryService queryService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('EVENT_VIEW')")
    public EventDashboardResponse dashboard() {
        return dashboardService.getDashboard();
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAuthority('EVENT_VIEW')")
    public List<EventResponse> upcoming() {
        return queryService.getUpcomingEvents();
    }

    @GetMapping("/past")
    @PreAuthorize("hasAuthority('EVENT_VIEW')")
    public List<EventResponse> past() {
        return queryService.getPastEvents();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('EVENT_VIEW')")
    public Page<EventResponse> search(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100)
        );

        return queryService.searchPublicEvents(query, pageable);
    }

    @GetMapping("/summaries")
    @PreAuthorize("hasAuthority('EVENT_VIEW')")
    public List<EventSummaryResponse> summaries() {
        return queryService.getEventSummaries();
    }

    @GetMapping("/{eventId}/summary")
    @PreAuthorize("hasAuthority('EVENT_VIEW')")
    public EventSummaryResponse summary(@PathVariable UUID eventId) {
        return queryService.getEventSummary(eventId);
    }
}