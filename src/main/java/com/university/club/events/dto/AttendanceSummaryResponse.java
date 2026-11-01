package com.university.club.events.dto;

import java.util.UUID;

public record AttendanceSummaryResponse(

        UUID eventId,

        String eventTitle,

        long totalApproved,

        long present,

        long absent,

        long notMarked,

        double attendanceRate

) {
}