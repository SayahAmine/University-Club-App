package com.university.club.events.controller;

import com.university.club.common.security.UserPrincipal;
import com.university.club.events.dto.AttendanceRequest;
import com.university.club.events.dto.AttendanceSummaryResponse;
import com.university.club.events.dto.RegistrationResponse;
import com.university.club.events.service.EventAttendanceService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events/{eventId}/attendance")
public class EventAttendanceController {

    private final EventAttendanceService attendanceService;

    public EventAttendanceController(
            EventAttendanceService attendanceService
    ) {
        this.attendanceService = attendanceService;
    }

    // Get attendance list
    @GetMapping
    @PreAuthorize("hasAuthority('EVENT_MANAGE_ATTENDANCE')")
    public List<RegistrationResponse> getAttendance(
            @PathVariable UUID eventId
    ) {

        return attendanceService.getAttendance(eventId);
    }

    // Get attendance statistics
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('EVENT_MANAGE_ATTENDANCE')")
    public AttendanceSummaryResponse getAttendanceSummary(
            @PathVariable UUID eventId
    ) {

        return attendanceService.getAttendanceSummary(eventId);
    }
}