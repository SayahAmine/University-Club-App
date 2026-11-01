package com.university.club.events.dto;

public record EventDashboardResponse(
        long totalEvents,
        long draftEvents,
        long publishedEvents,
        long upcomingEvents,
        long completedEvents,
        long cancelledEvents,
        long totalRegistrations,
        long pendingRegistrations,
        long approvedRegistrations
) {}